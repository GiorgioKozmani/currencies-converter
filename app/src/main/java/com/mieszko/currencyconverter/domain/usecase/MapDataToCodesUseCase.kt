package com.mieszko.currencyconverter.domain.usecase

import android.util.Log
import androidx.annotation.WorkerThread
import com.mieszko.currencyconverter.common.SupportedCode
import com.mieszko.currencyconverter.domain.model.CodeWithData
import com.mieszko.currencyconverter.domain.repository.ICodesDataRepository
import java.util.*

class MapDataToCodesUseCase(
    private val codesDataRepository: ICodesDataRepository
) : IMapDataToCodesUseCase {

    @WorkerThread
    override fun invoke(
        codes: List<SupportedCode>,
        allRatios: EnumMap<SupportedCode, Double>
    ): List<CodeWithData> =
        codes.mapNotNull { code ->
            // TODO CHECK THREADING!
            val toUahRatio = allRatios[code]
            //todo rename
            val data = codesDataRepository.getCodeStaticData(code)

            if (toUahRatio != null && data != null) {
                CodeWithData(code = code, toUahRatio = toUahRatio, data = data)
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
