// Copyright 2023, Christopher Banes and the Tivi project contributors
// SPDX-License-Identifier: Apache-2.0

@file:OptIn(ExperimentalMaterial3Api::class)

package app.tivi.common.compose.ui

import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@Composable
actual fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeChanged: (LocalTime) -> Unit,
    selectedTime: LocalTime,
    confirmLabel: String,
    title: String,
) {
    val timePickerState = rememberTimePickerState(selectedTime.hour, selectedTime.minute)

    LaunchedEffect(timePickerState) {
        snapshotFlow { LocalTime(timePickerState.hour, timePickerState.minute, 0, 0) }
            .collect { onTimeChanged(it) }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = onDismissRequest) {
                Text(text = confirmLabel)
            }
        },
        text = {
            TimePicker(state = timePickerState)
        },
    )
}

@Composable
actual fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateChanged: (LocalDate) -> Unit,
    selectedDate: LocalDate,
    confirmLabel: String,
    minimumDate: LocalDate?,
    maximumDate: LocalDate?,
    title: String,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
            .atTime(hour = 12, minute = 0)
            .toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds(),
        yearRange = 1900..Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.year,
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = onDismissRequest) {
                Text(text = confirmLabel)
            }
        },
        text = {
            DatePicker(
                state = datePickerState,
                dateValidator = { epoch ->
                    val date = Instant.fromEpochMilliseconds(epoch)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .date
                    when {
                        minimumDate != null && date < minimumDate -> false
                        maximumDate != null && date > maximumDate -> false
                        else -> true
                    }
                },
            )
        },
    )
}
