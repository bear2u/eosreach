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
package com.memtrip.eosreach.robot.account.actions

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.memtrip.eosreach.R
import com.memtrip.eosreach.api.actions.model.AccountAction
import com.memtrip.eosreach.uikit.SimpleAdapterViewHolder
import org.hamcrest.CoreMatchers.not

class ActionsRobot {

    fun verifyActionsScreen(): ActionsRobot {
        onView(withId(R.id.account_actions_toolbar))
            .check(matches(isDisplayed()))
        onView(withId(R.id.account_actions_navigation))
            .check(matches(isDisplayed()))
        onView(withId(R.id.account_actions_list_swiperefresh))
            .check(matches(isDisplayed()))
        return this
    }

    fun selectFirstActionRow(): ActionsRobot {
        onView(withId(R.id.account_actions_list_recyclerview))
            .perform(RecyclerViewActions.actionOnItemAtPosition<SimpleAdapterViewHolder<AccountAction>>(0, click()))
        return this
    }

    fun selectTransferButton(): ActionsRobot {
        onView(ViewMatchers.withId(R.id.account_actions_transfer_button))
            .check(matches(isDisplayed()))
            .perform(click())
        return this
    }

    fun verifyActionsReadOnlyScreen(): ActionsRobot {
        onView(withId(R.id.account_actions_toolbar))
            .check(matches(isDisplayed()))
        onView(withId(R.id.account_actions_navigation))
            .check(matches(not(isDisplayed())))
        return this
    }

    fun verifyViewTransferActionScreen(): ActionsRobot {
        onView(withId(R.id.account_actions_view_transfer_action_toolbar))
            .check(matches(isDisplayed()))
        onView(withId(R.id.account_actions_view_transfer_details))
            .check(matches(isDisplayed()))
        onView(withId(R.id.account_actions_view_transaction_block_explorer_button))
            .check(matches(isDisplayed()))
        return this
    }

    fun selectViewTransferActionFromAccountLabel(): ActionsRobot {
        onView(withId(R.id.transfer_details_from_value))
            .check(matches(isDisplayed()))
            .perform(click())
        return this
    }
}