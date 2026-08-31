package br.inf.cepp.financemanager.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.inf.cepp.financemanager.model.ProjectItem
import br.inf.cepp.financemanager.model.ProjectPlan
import br.inf.cepp.financemanager.repository.IFinanceService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ProjectsViewModel(private val financeService: IFinanceService) : ViewModel() {
    private val _plans = MutableStateFlow<List<ProjectPlan>>(emptyList())
    val plans: StateFlow<List<ProjectPlan>> = _plans.asStateFlow()

    private val _projectItems = MutableStateFlow<Map<String, List<ProjectItem>>>(emptyMap())
    val projectItems: StateFlow<Map<String, List<ProjectItem>>> = _projectItems.asStateFlow()

    init {
        refreshPlans()
    }

    fun refreshPlans() {
        viewModelScope.launch {
            val projects = financeService.getProjectPlans().first()
            _plans.value = projects
            val itemMap = projects.associate { plan ->
                plan.name to financeService.getProjectItems(plan.name).first()
            }
            _projectItems.value = itemMap
        }
    }

    fun savePlan(plan: ProjectPlan) {
        viewModelScope.launch {
            financeService.saveProjectPlan(plan)
            refreshPlans()
        }
    }

    fun saveItem(item: ProjectItem) {
        viewModelScope.launch {
            financeService.saveProjectItem(item)
            refreshPlans()
        }
    }

    fun deleteItem(item: ProjectItem) {
        viewModelScope.launch {
            financeService.deleteProjectItem(item)
            refreshPlans()
        }
    }
}
