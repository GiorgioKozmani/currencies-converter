package com.mieszko.currencyconverter.domain.usecase.mappers

import androidx.annotation.WorkerThread
import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.model.CodeWithData
import com.mieszko.currencyconverter.domain.repository.ICodesStaticDataRepository
import java.util.EnumMap

class MapDataToCodesUseCase(
    private val codesStaticDataRepository: ICodesStaticDataRepository
) : IMapDataToCodesUseCase {

    @WorkerThread
    override fun invoke(
        codes: List<SupportedCode>,
        allRatios: EnumMap<SupportedCode, Double>
    ): List<CodeWithData> = codes.mapNotNull { code ->
        val toUahRatio = allRatios[code]
        val data = codesStaticDataRepository.getCodeStaticData(code)

        if (toUahRatio != null && data != null) {
            CodeWithData(code = code, toUahRatio = toUahRatio, staticData = data)
        } else {
            null
        }
    }
}

interface IMapDataToCodesUseCase {
    operator fun invoke(
        codes: List<SupportedCode>,
        allRatios: EnumMap<SupportedCode, Double>
    ): List<CodeWithData>
}
