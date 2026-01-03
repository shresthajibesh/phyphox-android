package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.graphsize

import de.rwth_aachen.phyphox.features.settings.domain.usecase.graphsize.GetGraphSizeRangeUseCase
import de.rwth_aachen.phyphox.features.settings.domain.usecase.graphsize.ObserveCurrentGraphSizeUseCase
import de.rwth_aachen.phyphox.features.settings.domain.usecase.graphsize.UpdateGraphSizeUseCase
import de.rwth_aachen.phyphox.features.settings.presentation.compose.seekbarpreferenceitem.SeekBarConfig
import de.rwth_aachen.phyphox.utils.UiResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class DefaultGraphSizeDelegate @Inject constructor(
    private val observeCurrentGraphSize: ObserveCurrentGraphSizeUseCase,
    private val getGraphSizeRange: GetGraphSizeRangeUseCase,
    private val updateGraphSize: UpdateGraphSizeUseCase,
) : GraphSizeDelegate {
    private val currentGraphSizeFlow = MutableStateFlow<UiResourceState<Float>>(
        UiResourceState.Loading,
    )
    private val graphSizeRangeFlow =
        MutableStateFlow<UiResourceState<ClosedFloatingPointRange<Float>>>(
            UiResourceState.Loading,
        )

    override val graphSizeFlow: Flow<UiResourceState<SeekBarConfig>> = combine(
        currentGraphSizeFlow,
        graphSizeRangeFlow,
    ) { graphSize, range ->
        if (graphSize is UiResourceState.Success && range is UiResourceState.Success) {
            UiResourceState.Success(
                SeekBarConfig(
                    currentSize = graphSize.data,
                    range = range.data,
                ),
            )
        } else {
            UiResourceState.Loading
        }
    }
}
