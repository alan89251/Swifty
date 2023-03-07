package com.team2.handiwork.utilities

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

class Ext {
    companion object {

        fun Disposable.disposedBy(disposeBag: CompositeDisposable) {
            disposeBag.add(this)
        }
    }
}