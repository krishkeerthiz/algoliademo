package com.example.algoliademo1

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.core.selectable.list.SelectionMode
import com.algolia.instantsearch.helper.android.filter.state.connectPagedList
import com.algolia.instantsearch.helper.android.list.SearcherSingleIndexDataSource
import com.algolia.instantsearch.helper.android.searchbox.SearchBoxConnectorPagedList
import com.algolia.instantsearch.helper.filter.facet.FacetListConnector
import com.algolia.instantsearch.helper.filter.facet.FacetListPresenterImpl
import com.algolia.instantsearch.helper.filter.facet.FacetSortCriterion
import com.algolia.instantsearch.helper.filter.state.FilterState
import com.algolia.instantsearch.helper.hierarchical.HierarchicalConnector
import com.algolia.instantsearch.helper.searcher.SearcherSingleIndex
import com.algolia.instantsearch.helper.searcher.connectFilterState
import com.algolia.instantsearch.helper.stats.StatsConnector
import com.algolia.search.client.ClientSearch
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.Attribute
import com.algolia.search.model.IndexName
import io.ktor.client.features.logging.*
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class MyViewModel: ViewModel() {
    val client = ClientSearch(ApplicationID("9N1YDJJ8DK"), APIKey("dcd5088a151c2e8db47aec60ea0eb6ec"), LogLevel.ALL)
    val index = client.initIndex(IndexName("products2"))
    val searcher = SearcherSingleIndex(index)

    val dataSourceFactory = SearcherSingleIndexDataSource.Factory(searcher) { hit ->
        Product(
            //"Hello sample text"
            hit.json["name"].toString()
        )
    }

    val pagedListConfig = PagedList.Config.Builder().setPageSize(50).build()
    val products: LiveData<PagedList<Product>> = LivePagedListBuilder(dataSourceFactory, pagedListConfig).build()

    val searchBox = SearchBoxConnectorPagedList(searcher, listOf(products))

    val stats = StatsConnector(searcher)

    val filterState = FilterState()
    val facetList = FacetListConnector(
        searcher = searcher,
        filterState = filterState,
        attribute = Attribute("categories"),
        selectionMode = SelectionMode.Single
    )

    val facetList2 = FacetListConnector(
        searcher = searcher,
        filterState = filterState,
        attribute = Attribute("brand"),
        selectionMode = SelectionMode.Single
    )
    val facetPresenter = FacetListPresenterImpl(
        sortBy = listOf(FacetSortCriterion.CountDescending, FacetSortCriterion.IsRefined),
        limit = 100
    )

    val hierarchicalCategory = Attribute("hierarchicalCategories")
    val hierarchicalCategoryLvl0 = Attribute("$hierarchicalCategory.lvl0")
    val hierarchicalCategoryLvl1 = Attribute("$hierarchicalCategory.lvl1")
    val hierarchicalCategoryLvl2 = Attribute("$hierarchicalCategory.lvl2")
    val hierarchicalAttributes = listOf(
        hierarchicalCategoryLvl0,
        hierarchicalCategoryLvl1,
        hierarchicalCategoryLvl2
    )
    val separator = " > "
    val hierarchical = HierarchicalConnector(
        searcher = searcher,
        attribute = hierarchicalCategory,
        filterState = filterState,
        hierarchicalAttributes = hierarchicalAttributes,
        separator = separator
    )

    val connection = ConnectionHandler()

    init {
        connection += searchBox
        connection += stats

        connection += facetList
        connection += facetList2
        connection += searcher.connectFilterState(filterState)
        connection += filterState.connectPagedList(products)

    }

    override fun onCleared() {
        super.onCleared()
        searcher.cancel()
        connection.clear()
    }
}