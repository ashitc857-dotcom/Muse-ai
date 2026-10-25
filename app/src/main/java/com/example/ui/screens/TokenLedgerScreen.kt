package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Strings
import com.example.data.model.TransactionType
import com.example.ui.StudioViewModel
import com.example.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TokenLedgerScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val account by viewModel.ledgerManager.account.collectAsState()
    val transactions by viewModel.ledgerManager.transactions.collectAsState()
    val canClaim = viewModel.ledgerManager.canClaimDailyBonus()
    val videoCost by viewModel.ledgerManager.videoCost.collectAsState()
    val dailyBonus by viewModel.ledgerManager.dailyBonusAmount.collectAsState()

    val formattedTokens = remember(account.tokenBalance) {
        NumberFormat.getNumberInstance(Locale.US).format(account.tokenBalance)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        // Balance Header Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioAmber),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "TOTAL STUDIO TOKEN BALANCE",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$formattedTokens TOKENS",
                        color = StudioAmberLight,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = DarkSurfaceBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Daily Bonus Pool",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "+${NumberFormat.getNumberInstance().format(dailyBonus)} / Day",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { viewModel.claimDailyBonus() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canClaim) StudioAmber else DarkSurfaceVariant,
                                contentColor = if (canClaim) Color.Black else TextSecondary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("ledger_claim_daily_button")
                        ) {
                            Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (canClaim) "Claim Bonus" else Strings.get("claimed"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Token Rules & Transparency Explainer Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Text(
                        text = "STUDIO TOKEN ECONOMY RULES",
                        color = NeonCyanLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Rate: Exactly $videoCost Token per successful AI generation.\n" +
                               "• Atomic Accounting: Deducted only upon successful render completion.\n" +
                               "• Auto-Refund: Immediate 100% token refund on any model or network error.\n" +
                               "• Daily Reward: 10,000 tokens claimable once every 24 hours per account.\n" +
                               "• Promotional Balance: Tokens fuel full access to connected AI models.",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Ledger Transactions List Header
        item {
            Text(
                text = "TRANSACTION HISTORY & AUDIT LOG",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        // Ledger Items
        items(transactions) { tx ->
            val isCredit = tx.amount > 0
            val color = when (tx.type) {
                TransactionType.INITIAL_CREDIT, TransactionType.DAILY_BONUS -> StudioEmerald
                TransactionType.GENERATION_DEBIT -> StudioRose
                TransactionType.REFUND -> StudioAmber
                TransactionType.ADMIN_ADJUSTMENT -> NeonCyan
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tx.description,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(tx.timestamp)) +
                                   " • Balance after: ${NumberFormat.getNumberInstance().format(tx.balanceAfter)}",
                            color = TextTertiary,
                            fontSize = 10.sp
                        )
                    }

                    Text(
                        text = if (isCredit) "+${NumberFormat.getNumberInstance().format(tx.amount)}" else NumberFormat.getNumberInstance().format(tx.amount),
                        color = color,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
