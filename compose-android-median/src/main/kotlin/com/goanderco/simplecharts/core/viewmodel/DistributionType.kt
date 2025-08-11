package com.goanderco.simplecharts.core.viewmodel
import com.goanderco.R

enum class DistributionType(val code: String, val stringResID: Int) {
    NORM("norm", R.string.normal),
    UNIFORM("uniform", R.string.uniform),
    T("t",  R.string.t),
    GAMMA("gamma", R.string.gamma),
    EXP("exp", R.string.exponential),
    CHI2("chi2", R.string.chi_square)
}