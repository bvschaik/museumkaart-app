package nl.biancavanschaik.android.museumkaart.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import android.util.Log
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import nl.biancavanschaik.android.museumkaart.util.Resource
import nl.biancavanschaik.android.museumkaart.util.rest.ApiEmptyResponse
import nl.biancavanschaik.android.museumkaart.util.rest.ApiErrorResponse
import nl.biancavanschaik.android.museumkaart.util.rest.ApiResponse
import nl.biancavanschaik.android.museumkaart.util.rest.ApiSuccessResponse

abstract class CachedResource<ResultType, NetworkType> @MainThread constructor() {

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading()
        Log.d("CACHE", "Loading item from cache")
        @Suppress("LeakingThis")
        val cacheSource = loadFromCache()
        result.addSource(cacheSource) { data ->
            result.removeSource(cacheSource)
            Log.d("CACHE", "Loaded item from cache: $data")
            if (shouldFetch(data)) {
                Log.d("CACHE", "Loading (outdated) item from network")
                fetchFromNetwork(cacheSource)
            } else {
                result.addSource(cacheSource) { newData ->
                    setValue(Resource.success(newData))
                }
            }
        }
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { newData ->
            setValue(Resource.loading(newData))
        }
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            when (response) {
                is ApiSuccessResponse -> {
                    launch(CommonPool) {
                        saveCallResult(processResponse(response), dbSource.value)
                        launch(UI) {
                            // we specially request a new live data,
                            // otherwise we will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            result.addSource(loadFromCache()) { newData ->
                                setValue(Resource.success(newData))
                            }
                        }
                    }
                }
                is ApiEmptyResponse -> {
                    launch(UI) {
                        // reload from disk whatever we had
                        result.addSource(loadFromCache()) { newData ->
                            setValue(Resource.success(newData))
                        }
                    }
                }
                is ApiErrorResponse -> {
                    onFetchFailed()
                    result.addSource(dbSource) { newData ->
                        setValue(Resource.error(response.errorMessage, newData))
                    }
                }
            }
        }
    }

    protected open fun onFetchFailed() {}

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<NetworkType>) = response.body

    @WorkerThread
    protected abstract fun saveCallResult(item: NetworkType, cacheItem: ResultType?)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromCache(): LiveData<ResultType>

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<NetworkType>>
}