package com.team2.handiwork.base.fragment

import androidx.fragment.app.Fragment
import io.reactivex.rxjava3.disposables.CompositeDisposable

open class DisposeFragment: Fragment() {
    var disposeBag = CompositeDisposable()


    override fun onDestroy() {
        disposeBag.dispose()
        super.onDestroy()
    }
}