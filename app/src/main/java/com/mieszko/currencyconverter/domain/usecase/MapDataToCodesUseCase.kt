package com.mieszko.currencyconverter.domain.usecase

import androidx.annotation.WorkerThread
import com.mieszko.currencyconverter.common.SupportedCode
import com.mieszko.currencyconverter.domain.model.CodeWithData
import com.mieszko.currencyconverter.domain.repository.ICodesDataRepository

class MapDataToCodesUseCase(
    private val codesDataRepository: ICodesDataRepository
) : IMapDataToCodesUseCase {

    //TODO INVESTIGATE THIS
    // START USING THESE
    @WorkerThread
    override fun invoke(codes: List<SupportedCode>): List<CodeWithData> =
        codes.map { code ->
            // TODO CHECK THREADING!
            // TODO INTRODUCE SAFE MECHANISM SO LACK OF DATA DOESN'T CRASH THE APP
            CodeWithData(code, codesDataRepository.getCodeData(code))
        }

}

interface IMapDataToCodesUseCase {
    operator fun invoke(codes: List<SupportedCode>): List<CodeWithData>
}
