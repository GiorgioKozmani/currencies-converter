package com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.repository.ITrackedCodesRepository
import io.reactivex.rxjava3.core.Single

// TODO RETHINK WHOLE CONCENT
// https://proandroiddev.com/anemic-repositories-mvi-and-rxjava-induced-design-damage-and-how-aac-viewmodel-is-silently-1762caa70e13
// // still an anti-pattern (technically code smell)
// class DeleteUserByIdUseCase(
//    private val userDao: UserDao
// ) {
//    fun deleteUserById(userId: String) {
//        userDao.deleteUserById(userId)
//    }
// }

class GetTrackedCodesOnceUseCase(
    private val trackedCodesRepository: ITrackedCodesRepository
) : IGetTrackedCodesOnceUseCase {

    // todo consider threading
    override fun invoke(): Single<List<SupportedCode>> =
        trackedCodesRepository
            .getTrackedCodesOnce()
}

interface IGetTrackedCodesOnceUseCase {
    operator fun invoke(): Single<List<SupportedCode>>
}
