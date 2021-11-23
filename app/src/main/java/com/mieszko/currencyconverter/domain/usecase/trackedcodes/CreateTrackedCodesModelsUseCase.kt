package com.mieszko.currencyconverter.domain.usecase.trackedcodes

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.model.list.TrackingCurrenciesModel
import com.mieszko.currencyconverter.domain.repository.ICodesStaticDataRepository
import io.reactivex.rxjava3.core.Single

class CreateTrackedCodesModelsUseCase(private val codesStaticDataRepository: ICodesStaticDataRepository) :
    ICreateTrackedCodesModelsUseCase {

    // TODO [FUTURE IMPROVEMENT]
    // ONLY GET CURRENCIES THAT WE HAVE RATIOS AND DATA FOR? (NEW DEFINITION OF SUPPORTED CURRENCY)
    // NEW MODEL FOR THIS CASE CONTAINING COUNTRY (COUNTRIES?) SO WE CAN QUERY AGAINST THEM
    override fun invoke(trackedCurrencies: List<SupportedCode>): Single<List<TrackingCurrenciesModel>> =
        Single.fromCallable {
            val allCurrencies = SupportedCode.values()

            val notTrackedCodes = allCurrencies
                .subtract(trackedCurrencies)

            // this way we persist the order of tracked currencies
            mutableListOf<SupportedCode>().apply {
                addAll(trackedCurrencies)
                addAll(notTrackedCodes)
            }
                .mapNotNull { code ->
                    codesStaticDataRepository.getCodeStaticData(code)?.let { staticData ->
                        TrackingCurrenciesModel(
                            code,
                            staticData,
                            trackedCurrencies.contains(code)
                        )
                    }
                }
        }
}

interface ICreateTrackedCodesModelsUseCase {
    operator fun invoke(trackedCurrencies: List<SupportedCode>): Single<List<TrackingCurrenciesModel>>
}
