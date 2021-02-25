package com.mieszko.currencyconverter.data.repository

import com.mieszko.currencyconverter.common.Resource
import com.mieszko.currencyconverter.common.SupportedCode
import com.mieszko.currencyconverter.data.api.CurrenciesApi
import com.mieszko.currencyconverter.data.persistance.cache.ratios.IRatiosCache
import com.mieszko.currencyconverter.data.persistance.cache.ratios.RatiosTimeDTO
import com.mieszko.currencyconverter.domain.model.RatiosTime
import com.mieszko.currencyconverter.domain.repository.IRatiosRepository
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

class RatiosRepository(
    private val currenciesApi: CurrenciesApi,
    private val cache: IRatiosCache
) : IRatiosRepository {

    // TODO INTO OBSERVABLE
    override fun loadCurrenciesRatios(): Single<Resource<RatiosTime>> {
        //todo this needs to be tested
        // TODO HANDLE THREADING CORRECTLY HERE, I HAD A CASE THAT CHINA ICON DIDN'T WANT TO LOAD
        return currenciesApi
            .getCurrencies()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .doOnSuccess { cache.cacheRatios(it) }
            // TODO IF IT IS THE CASE, SHOW TOAST FOR NOW, IN FUTURE EMPLATELIKE
            .map { RatiosTimeDTO(it, Date().time) }
//            .doOnError { as long as it's before onerrorresumenext it's going to trigger. think of logging to crashlitics }
            .onErrorResumeNext { cache.getCachedRatios() }
            .flatMap<Resource<RatiosTime>> { ratiosTimeDTO ->
                Single.fromCallable {
                    val supportedRatios: EnumMap<SupportedCode, Double> =
                        EnumMap(SupportedCode::class.java)

                    ratiosTimeDTO
                        .ratios
                        .apply {
                            supportedRatios[SupportedCode.UAH] = 1.0
                        }
                        .forEach {
                            try {
                                supportedRatios[it.code] = it.ratioToUAH
                            } catch (e: Exception) {
                                // TODO CRASHLITICS
                            }
                        }

                    Resource.Success(RatiosTime(supportedRatios, Date(ratiosTimeDTO.time)))
                }
                    .subscribeOn(Schedulers.computation())
            }
            // cache failed to return value too
            .onErrorResumeNext { Single.just(Resource.Error("both network and cached fallback failed")) }

    }
}


