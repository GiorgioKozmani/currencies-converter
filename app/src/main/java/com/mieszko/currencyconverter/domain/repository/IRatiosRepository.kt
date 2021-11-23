package com.mieszko.currencyconverter.domain.repository

import com.mieszko.currencyconverter.data.model.RatiosTimeDTO
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface IRatiosRepository {
    // todo this is clearly wrong, repository should not expose DTO.
    // Once there's a time for it - remove the repository and just use use use case.
    fun observeRatios(): Observable<RatiosTimeDTO>
    fun fetchRemoteRatios(): Completable
}
