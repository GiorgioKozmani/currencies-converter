package com.mieszko.currencyconverter.domain.usecase

import com.mieszko.currencyconverter.common.Resource
import com.mieszko.currencyconverter.domain.model.RatiosTime
import com.mieszko.currencyconverter.domain.repository.IRatiosRepository
import io.reactivex.rxjava3.core.Single

//TODO DI
class GetRatiosAndTimeUseCase(
    private val ratiosRepository: IRatiosRepository
) : IGetRatiosAndTimeUseCase {

    //todo this repo should be talking to other usecases
    override fun getRatiosAndTime(): Single<Resource<RatiosTime>> {
        TODO("Not yet implemented")

        //todo use startwith loading? :>
        //todo in case of error emit Resource.error with DATA?
        // if no data just show error, if cached data's present show that it's outdated

        ratiosRepository
            .loadCurrenciesRatios()
    }
}

interface IGetRatiosAndTimeUseCase {
    fun getRatiosAndTime(): Single<Resource<RatiosTime>>
}