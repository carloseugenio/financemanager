package br.inf.cepp.financemanager.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import br.inf.cepp.financemanager.model.MonthlyExpensePerCategoryViewData
import br.inf.cepp.financemanager.ui.screen.CategoryProgressRow
import br.inf.cepp.financemanager.util.FakePlatformUtils
import br.inf.cepp.financemanager.util.LocalPlatformUtils
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home

class FinanceDashboardTest {

    @OptIn(ExperimentalTestApi::class)
//    @Test
    fun testDashboardRendersCorrectCurrencyAndDate() = runComposeUiTest {
        // 1. Arrange: Setup your mock rules for Brazilian Real layout scenario
        val fakeUtils = FakePlatformUtils(
            mockCurrencySymbol = "R$",
            mockFormattedDate = "Agosto 2026"
        )

        // 2. Act: Render your dashboard tree inside the fake provider scope
        setContent {
            CompositionLocalProvider(LocalPlatformUtils provides fakeUtils) {
                // Pass sample mock values to the UI row component we built earlier
                CategoryProgressRow(
                    category = MonthlyExpensePerCategoryViewData(
                        name = "Moradia",
                        amount = 1800.0,
                        percentage = 78.0,
                        color = androidx.compose.ui.graphics.Color(0xFF4F46E5),
                        icon = Icons.Filled.Home
                    )
                )
            }
        }

        // 3. Assert: Verify the strings pulled from the interface are present on screen
        onNodeWithText("Agosto 2026").assertIsDisplayed()
        onNodeWithText("R$ 1.800,00").assertIsDisplayed()
        onNodeWithText("Moradia").assertIsDisplayed()
        onNodeWithText("78%").assertIsDisplayed()
    }
}
