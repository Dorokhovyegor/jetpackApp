package com.dorokhov.jetpackapp.ui

// оборачиваем viewState Этим классом
data class DataState<T>(
    var error: Event<StateError>? = null,
    var loading: Loading = Loading(false),
    var data: Data<T>? = null
) {
    companion object {

        fun <T> error(
            response: Response
        ): DataState<T> {
            return DataState(
                error = Event(
                    StateError(
                        response
                    )
                )
            )
        }

        // во время загрузки может понадобиться кешированные данные, пока они грузятся, приложение
        // показывает прогресс бар, данные и одновременно грузит новые.
        fun <T> loading(
            isLoading: Boolean,
            cashedData: T? = null
        ): DataState<T> {
            return DataState(
                error = null,
                loading = Loading(isLoading),
                data = Data(
                    Event.dataEvent(
                        cashedData
                    ),
                    null
                )
            )
        }

        fun <T> data(
            data: T? = null,
            response: Response? = null
        ): DataState<T> {
            return DataState(
                data = Data(
                    Event.dataEvent(data),
                    Event.responseEvent(response)
                )
            )
        }
    }
}