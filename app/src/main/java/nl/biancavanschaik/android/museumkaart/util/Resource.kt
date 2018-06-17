package nl.biancavanschaik.android.museumkaart.util

data class Resource<T> constructor(
        val status: Status,
        val data: T? = null,
        val message: String? = null
) {

    enum class Status {
        LOADING,
        ERROR,
        SUCCESS
    }

    companion object {
        fun <T> success(data: T?) = Resource(Status.SUCCESS, data)
        fun <T> error(msg: String, data: T? = null) = Resource(Status.ERROR, data, msg)
        fun <T> loading(data: T? = null): Resource<T> = Resource(Status.LOADING, data)
    }
}