package com.mieszko.currencyconverter.domain.usecase

import androidx.annotation.WorkerThread
import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.model.CodeWithData
import com.mieszko.currencyconverter.domain.repository.ICodesDataRepository
import java.util.EnumMap

class MapDataToCodesUseCase(
    private val codesDataRepository: ICodesDataRepository
) : IMapDataToCodesUseCase {

    @WorkerThread
    override fun invoke(
        codes: List<SupportedCode>,
        allRatios: EnumMap<SupportedCode, Double>
    ): List<CodeWithData> = codes.mapNotNull { code ->
        val toUahRatio = allRatios[code]
        val data = codesDataRepository.getCodeStaticData(code)

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
