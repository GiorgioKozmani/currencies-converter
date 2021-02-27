package com.mieszko.currencyconverter.domain.usecase.ratios

import com.mieszko.currencyconverter.common.Resource
import com.mieszko.currencyconverter.common.SupportedCode
import com.mieszko.currencyconverter.domain.model.RatiosTime
import com.mieszko.currencyconverter.domain.repository.IRatiosRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.*

//TODO REWORK! SO IT RETURNS IT TOGETHER WITH THE DATA!!!
class ObserveRatiosUseCase(
    private val ratiosRepository: IRatiosRepository
) : IObserveRatiosUseCase {

    override fun invoke(): Observable<Resource<RatiosTime>> =
        ratiosRepository.observeRatios()
            //todo consider switchmap?
            //TODO RETHINK HANDLING RESOURCES WRAPPER
                //TODO GET THREADING RIGHT
            .flatMapSingle { ratios ->
                //todo check thread
                when(ratios){
                    is Resource.Success -> {
                        Single.fromCallable {
                            val supportedRatios: EnumMap<SupportedCode, Double> =
                                EnumMap(SupportedCode::class.java)

                            ratios.data!!
                                .ratios
                                .also { supportedRatios[SupportedCode.UAH] = 1.0 }
                                .forEach {
                                    try {
                                        supportedRatios[it.code] = it.ratioToUAH
                                    } catch (e: Exception) {
                                        // TODO CRASHLITICS
                                    }
                                }

                            Resource.Success(RatiosTime(supportedRatios, ratios.data.date))
                        }
                    }
                    //TODO DEAL WITH LOADING!! REMOVE RESOURCE FROM THERE
                    is Resource.Loading, is Resource.Error -> {
                        Single.just(Resource.Error("error"))
                    }
                }
            }
    //todo add start with loading when it'll be observable
    // cache failed to return value too
//            .onErrorResumeNext { Observable.just(Resource.Error("both network and cached fallback failed")) }
}

interface IObserveRatiosUseCase {
    //todo into observable? then it should always come from one source (disc)
    operator fun invoke(): Observable<Resource<RatiosTime>>
}
