package com.mieszko.currencyconverter.presentation.home

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mieszko.currencyconverter.common.BaseViewModel
import com.mieszko.currencyconverter.common.IDisposablesBag
import com.mieszko.currencyconverter.common.Resource
import com.mieszko.currencyconverter.common.SupportedCode
import com.mieszko.currencyconverter.domain.model.CodeWithData
import com.mieszko.currencyconverter.domain.model.RatiosTime
import com.mieszko.currencyconverter.domain.model.list.HomeListModel
import com.mieszko.currencyconverter.domain.usecase.IMapDataToCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.ratios.IFetchRemoteRatiosUseCase
import com.mieszko.currencyconverter.domain.usecase.ratios.IObserveRatiosUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.IMoveTrackedCodeToTopUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.ISwapTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.IObserveTrackedCodesUseCase
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject
import java.net.UnknownHostException
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToLong

// TODO
//https://proandroiddev.com/anemic-repositories-mvi-and-rxjava-induced-design-damage-and-how-aac-viewmodel-is-silently-1762caa70e13
//The take-away here is that if:
//you don’t have a saveState/restoreState function on your ViewModel
//you don’t have an initialState: Bundle? constructor argument on your ViewModel (passed in from the ViewModelProvider.Factory in your Activity/Fragment as a dynamic argument)
//you don’t use SavedStateHandle.getLiveData("key") nor SavedStateHandle.get("key")/set("key")
// https://developer.android.com/jetpack/androidx/releases/lifecycle#version_230_3
// there are some changes to saving state made recently

class HomeViewModel(
    disposablesBag: IDisposablesBag,
    observeRatiosUseCase: IObserveRatiosUseCase,
    observeTrackedCodesUseCase: IObserveTrackedCodesUseCase,
    fetchRemoteRatiosUseCase: IFetchRemoteRatiosUseCase,
    private val moveTrackedCodeToTopUseCase: IMoveTrackedCodeToTopUseCase,
    private val swapTrackedCodesUseCase: ISwapTrackedCodesUseCase,
    private val mapCodeToDataUseCase: IMapDataToCodesUseCase
) : BaseViewModel(disposablesBag) {

    private val currenciesListModelsLiveData: MutableLiveData<Resource<List<HomeListModel>>> =
        MutableLiveData()
    private val lastUpdatedLiveData: MutableLiveData<Resource<Date>> =
        MutableLiveData()

    //Exposing only LiveData
    fun getCurrenciesLiveData(): LiveData<Resource<List<HomeListModel>>> =
        currenciesListModelsLiveData

    fun getLastUpdatedLiveData(): LiveData<Resource<Date>> =
        lastUpdatedLiveData

    private val baseAmountChange: Subject<Double> =
        BehaviorSubject.createDefault(DEFAULT_BASE_AMOUNT)

    // models that are being passed to the view
    // TODO DON'T STORE ACTUAL MODELS HERE? STORY ONLY DATA MODELS SO THIGS THAT COME FROM REPOSITORIES
    // 1. CODE 2. NAME 3. FLAG 4. TOUAHRATIO 5. AMOUNT?
    //TODO !! STEP AWAY FROM KEEPING A LIST OF currenciesListModels IN THE VIEWMODEL, AND KEEP ONLY DATA MODELS HERE?
    // ACTUAL MODELS SENT TO THE VIEW WOULD BE GENERATED JUST BEFORE EMISSION
    // TODO HANDLE ALL !!s
    private var currenciesListModels = listOf<HomeListModel>()

    init {
        // emit list items when ratios or tracking order or base amount changes
        disposablesBag.add(
            //todo use RxRelay with default of loading?
            Observable.combineLatest(
                // CODES RATIOS CHANGED
                observeRatiosUseCase()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    //TODO HANDLE ERROR HERE?
                    .ofType(Resource.Success::class.java)
                    //todo into entity
                    .map { ratios ->
                        Pair(
                            System.nanoTime(),
                            ratios as Resource.Success<RatiosTime>
                        )
                    },
                // TRACKING CODES CHANGED
                observeTrackedCodesUseCase()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .map { trackedCodes -> Pair(System.nanoTime(), trackedCodes) },
                // BASE CURRENCY AMOUNT CHANGED
                baseAmountChange
                    .subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.computation())
                    .map { newAmount -> Pair(System.nanoTime(), newAmount) },
                { ratios, trackedCodes, newBaseAmount ->
                    //todo map before so it's not passing resource anymore
                    val allCodesRatios = ratios.second.data!!.ratios

                    // CHECK WHAT'S THE LATEST CHANGE
                    when (listOf(ratios, trackedCodes, newBaseAmount).maxByOrNull { it.first }) {
                        ratios -> {
                            // recreate all models on ratios change
                            makeListItemModels(
                                mapCodeToDataUseCase(trackedCodes.second),
                                allCodesRatios
                            ).subscribeOn(Schedulers.computation())
                        }
                        trackedCodes -> {
                            when {
                                //todo step away from pair wrapping as it's not readable now
                                trackedCodes.second.isEmpty() -> {
                                    // when there's no tracked currencies emit no items
                                    Single.just(listOf<HomeListModel>())
                                        .subscribeOn(Schedulers.computation())
                                }
                                baseStaysTheSame(trackedCodes.second) && itemsDidNotChange(
                                    trackedCodes.second
                                ) -> {
                                    repositionNonBaseItems(trackedCodes.second)
                                        .subscribeOn(Schedulers.computation())
                                }
                                // TODO THIS TODO IS IMPORTANT :D add case for adding / removing some when base haven't changed!
                                else -> {
                                    makeListItemModels(
                                        mapCodeToDataUseCase(trackedCodes.second),
                                        allCodesRatios
                                    ).subscribeOn(Schedulers.computation())
                                }
                            }
                        }
                        newBaseAmount -> {
                            //TODO THIS WILL CRASH WHEN LIST IS EMPTY
                            if (currenciesListModels.isEmpty()) {
                                Single.just(listOf())
                            } else {
                                val baseToUahRatio =
                                    allCodesRatios[currenciesListModels.first().code]!!
                                recreateCurrentItems(
                                    newBaseAmount.second,
                                    baseToUahRatio,
                                    allCodesRatios
                                ).subscribeOn(Schedulers.computation())
                            }
                        }
                        // FALLBACK - recreate list models. This should never be reached
                        else -> {
                            makeListItemModels(
                                mapCodeToDataUseCase(trackedCodes.second),
                                allCodesRatios
                            ).subscribeOn(Schedulers.computation())
                        }
                    }
                }
            )
                .flatMapSingle { it }
                .doOnNext { listItems -> currenciesListModels = listItems }
                .subscribeBy(
                    onNext = { listItems -> emitModels(listItems) },
                    onError = { emitError(it) }
                ))

        // ATTEMPT FETCHING FRESH DATA FROM NETWORK
        disposablesBag.add(
            fetchRemoteRatiosUseCase()
                .subscribeOn(Schedulers.io())
                .subscribe()
        )
    }

    /**
     * returns a new list that mapped currenciesListModels items to tracked order
     *
     * @param trackedCodes
     */
    private fun repositionNonBaseItems(trackedCodes: List<SupportedCode>) =
        Single.fromCallable {
            // copy models list
            val reorderedList = currenciesListModels.toList()
            trackedCodes.forEachIndexed { index, supportedCurrency ->
                if (supportedCurrency != reorderedList[index].code) {
                    Collections.swap(
                        reorderedList,
                        index,
                        reorderedList.indexOfFirst { it.code == supportedCurrency }
                    )
                }
            }
            reorderedList
        }

    fun setBaseCurrencyAmount(newAmount: Double) {
        baseAmountChange.onNext(newAmount)
    }

    @WorkerThread
    private fun recreateCurrentItems(
        baseAmount: Double,
        baseToUahRatio: Double,
        allCodesRatios: EnumMap<SupportedCode, Double>
    ): Single<List<HomeListModel>> =
        Single.fromCallable {
            currenciesListModels.map { currentItem ->
                when (currentItem) {
                    is HomeListModel.Base -> currentItem.copy(amount = baseAmount)
                    is HomeListModel.Regular -> currentItem.copy(
                        amount = calculateCurrencyAmount(
                            currToUahRatio = allCodesRatios[currentItem.code]!!,
                            baseToUahRatio = baseToUahRatio,
                            baseAmount = baseAmount
                        )
                    )
                }
            }
        }

    fun setBaseCurrency(newBaseCurrency: SupportedCode) {
        moveTrackedCodeToTopUseCase(newBaseCurrency)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun moveItem(from: Int, to: Int) {
        swapTrackedCodesUseCase(
            currenciesListModels[from].code,
            currenciesListModels[to].code
        )
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    @WorkerThread
    private fun calculateCurrencyAmount(
        currToUahRatio: Double,
        baseToUahRatio: Double,
        baseAmount: Double
    ) =
        try {
            (baseToUahRatio / currToUahRatio * baseAmount)
                .roundToDecimals(2)
        } catch (t: Throwable) {
            //todo TO CRASHLYTICS
            0.0
        }

//    private fun emitUpdateDate() {
//        //todo revise
//     val dateLong = SharedPrefsManager.getLong(SharedPrefsManager.Key.CachedCurrenciesTime)
//     val dateResource =
//         if (dateLong != (-1).toLong()) {
//             Resource.Success(Date(dateLong))
//         } else {
//             Resource.Error("Never updated")
//         }
//     lastUpdatedLiveData.postValue(dateResource)
//    }

    private fun emitError(t: Throwable?) {
        //todo strings, revise

        // REVISE
        val errorMessage = when (t) {
            is UnknownHostException -> {
                "Not updated, turn on internet."
            }
            is NoSuchElementException -> {
                "Nothing to show, turn on internet."
            }
            else -> {
                "Error occurred"
            }
        }
        currenciesListModelsLiveData.postValue(Resource.Error(errorMessage))
    }

    private fun emitModels(listItemsModels: List<HomeListModel>) {
        //todo step away from emitting resource from here
        currenciesListModelsLiveData.postValue(Resource.Success(listItemsModels))
    }

    //TODO IT WOULD BE OPTIMAL TO MAKE CODE WITH DATA CONTAIN ALSO FRESH RATIO FOR THAT CURRENCY
    //TODO ADD BASE TO PARAMS
    @WorkerThread
    private fun makeListItemModels(
        trackedCodesWithData: List<CodeWithData>,
        allCodesRatios: EnumMap<SupportedCode, Double>
    ): Single<List<HomeListModel>> {
        if (trackedCodesWithData.isEmpty()) {
            return Single.just(listOf())
        }

        //TODO RETHINK IT
        var baseCurrency = currenciesListModels.firstOrNull()
            ?: let {
                //TODO THIS CRASHES WHEN LIST IS EMPTY
                val newBaseCodeWithData = trackedCodesWithData.first()
                HomeListModel.Base(
                    newBaseCodeWithData.code,
                    newBaseCodeWithData.data,
                    DEFAULT_BASE_AMOUNT
                )
            }

        return Single.fromCallable {
            trackedCodesWithData
                .mapIndexed { index, trackedCodeWithData ->
                    //TODO THINK OF HOW TO GET A RID OF !!
                    val newCurrencyRatio = allCodesRatios[trackedCodeWithData.code]!!
                    val baseRatio = allCodesRatios[baseCurrency.code]!!
                    val baseAmount = baseCurrency.amount

                    if (index == 0) {
                        HomeListModel.Base(
                            code = trackedCodeWithData.code,
                            codeData = trackedCodeWithData.data,
                            amount = calculateCurrencyAmount(
                                currToUahRatio = newCurrencyRatio,
                                baseToUahRatio = baseRatio,
                                baseAmount = baseAmount
                            )
                        ).also { baseCurrency = it }
                    } else {
                        val baseCurrencyCode = baseCurrency.code

                        HomeListModel.Regular(
                            code = trackedCodeWithData.code,
                            codeData = trackedCodeWithData.data,
                            amount = calculateCurrencyAmount(
                                currToUahRatio = newCurrencyRatio,
                                baseToUahRatio = baseRatio,
                                baseAmount = baseAmount
                            ),
                            baseToThisText = makeRatioString(
                                firstCurrencyCode = trackedCodeWithData.code,
                                firstCurrencyToUahRatio = newCurrencyRatio,
                                secondCurrencyCode = baseCurrencyCode,
                                secondCurrencyToUahRatio = baseRatio
                            ),
                            thisToBaseText = makeRatioString(
                                firstCurrencyCode = baseCurrencyCode,
                                firstCurrencyToUahRatio = baseRatio,
                                secondCurrencyCode = trackedCodeWithData.code,
                                secondCurrencyToUahRatio = newCurrencyRatio
                            )
                        )
                    }
                }
        }
    }

    //todo extract to usecase
    @WorkerThread
    private fun makeRatioString(
        firstCurrencyCode: SupportedCode,
        firstCurrencyToUahRatio: Double,
        secondCurrencyCode: SupportedCode,
        secondCurrencyToUahRatio: Double
    ) = if (secondCurrencyToUahRatio != 0.0) {
        "1 $firstCurrencyCode ≈ ${
            (firstCurrencyToUahRatio / secondCurrencyToUahRatio)
                .roundToDecimals(3)
        } $secondCurrencyCode"
    } else {
        //todo TO CRASHLYTICS
        ""
    }

    @WorkerThread
    private fun Double.roundToDecimals(decimals: Int): Double {
        val factor = 10.0.pow(decimals)
        return (this * factor).roundToLong() / factor
    }

    @WorkerThread
    private fun itemsDidNotChange(trackedCurrencies: List<SupportedCode>): Boolean =
        trackedCurrencies.size == currenciesListModels.size
                && trackedCurrencies.containsAll(currenciesListModels.map { it.code })

    @WorkerThread
    private fun baseStaysTheSame(trackedCurrencies: List<SupportedCode>): Boolean =
        trackedCurrencies.first() == currenciesListModels.firstOrNull()?.code

    private companion object {
        const val DEFAULT_BASE_AMOUNT = 100.0
    }
}