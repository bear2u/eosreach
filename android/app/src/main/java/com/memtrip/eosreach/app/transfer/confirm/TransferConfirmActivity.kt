/*
Copyright (C) 2018-present memtrip

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.memtrip.eosreach.app.transfer.confirm

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.jakewharton.rxbinding2.view.RxView
import com.memtrip.eosreach.R
import com.memtrip.eosreach.api.transfer.ActionReceipt
import com.memtrip.eosreach.app.MviActivity
import com.memtrip.eosreach.app.ViewModelFactory
import com.memtrip.eosreach.app.account.AccountTheme
import com.memtrip.eosreach.app.price.BalanceFormatter
import com.memtrip.eosreach.app.transaction.log.TransactionLogActivity.Companion.transactionLogIntent
import com.memtrip.eosreach.app.transaction.receipt.TransactionReceiptActivity.Companion.transactionReceiptIntent
import com.memtrip.eosreach.app.transaction.receipt.TransactionReceiptRoute
import com.memtrip.eosreach.app.transfer.form.TransferFormData
import com.memtrip.eosreach.uikit.gone
import com.memtrip.eosreach.uikit.invisible
import com.memtrip.eosreach.uikit.visible
import dagger.android.AndroidInjection
import io.reactivex.Observable
import kotlinx.android.synthetic.main.transfer_confirm_activity.*
import javax.inject.Inject

class TransferConfirmActivity
    : MviActivity<TransferConfirmIntent, TransferConfirmRenderAction, TransferConfirmViewState, TransferConfirmViewLayout>(), TransferConfirmViewLayout {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var render: TransferConfirmViewRenderer

    lateinit var transferFormData: TransferFormData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transfer_confirm_activity)
        setSupportActionBar(transfer_confirm_toolbar)
        supportActionBar!!.title = getString(R.string.transfer_confirm_toolbar_title)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        transferFormData = transferFormDataExtra(intent)
    }

    override fun inject() {
        AndroidInjection.inject(this)
    }

    override fun intents(): Observable<TransferConfirmIntent> = Observable.merge(
        Observable.just(TransferConfirmIntent.Init(transferFormData)),
        RxView.clicks(transfer_confirm_confirm_button).map {
            TransferConfirmIntent.Transfer(
                TransferRequestData(
                    transferFormData.contractAccountBalance.accountName,
                    transferFormData.toAccountName,
                    BalanceFormatter.formatEosBalance(
                        transferFormData.amount,
                        transferFormData.contractAccountBalance.balance.symbol),
                    transferFormData.memo,
                    transferFormDataExtra(intent).contractAccountBalance
                )
            )
        }
    )

    override fun layout(): TransferConfirmViewLayout = this

    override fun model(): TransferConfirmViewModel = getViewModel(viewModelFactory)

    override fun render(): TransferConfirmViewRenderer = render

    override fun populate(transferFormData: TransferFormData) {
        transfer_confirm_form_details.populate(
            BalanceFormatter.create(
                transferFormData.amount,
                transferFormData.contractAccountBalance.balance.symbol),
            transferFormData.toAccountName,
            transferFormData.contractAccountBalance.accountName,
            transferFormData.memo,
            AccountTheme.DEFAULT,
            transferFormData.contractAccountBalance
        )
    }

    override fun showProgress() {
        transfer_confirm_progress.visible()
        transfer_confirm_confirm_button.invisible()
    }

    override fun onSuccess(transferReceipt: ActionReceipt) {
        startActivity(transactionReceiptIntent(
            transferReceipt,
            transferFormData.contractAccountBalance,
            TransactionReceiptRoute.ACTIONS,
            this))
        finish()
    }

    override fun showError(message: String, log: String) {
        transfer_confirm_progress.gone()
        transfer_confirm_confirm_button.visible()

        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(R.string.transaction_view_log_position_button) { _, _ ->
                model().publish(TransferConfirmIntent.ViewLog(log))
            }
            .setNegativeButton(R.string.transaction_view_log_negative_button, null)
            .create()
            .show()
    }

    override fun viewLog(log: String) {
        model().publish(TransferConfirmIntent.Idle)
        startActivity(transactionLogIntent(log, this))
    }

    companion object {

        private const val TRANSFER_FORM_EXTRA = "TRANSFER_FORM_EXTRA"

        fun transferConfirmIntent(transferFormData: TransferFormData, context: Context): Intent {
            return with (Intent(context, TransferConfirmActivity::class.java)) {
                putExtra(TRANSFER_FORM_EXTRA, transferFormData)
                this
            }
        }

        fun transferFormDataExtra(intent: Intent): TransferFormData {
            return intent.getParcelableExtra(TRANSFER_FORM_EXTRA) as TransferFormData
        }
    }
}
