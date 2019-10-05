package com.egroden.sample.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.egroden.mvico.Connector
import com.egroden.mvico.bindAction
import com.egroden.mvico.connect
import com.egroden.mvico.disconnect
import com.egroden.sample.*

class MovieFragment(
    private val connector: Connector<Action, SideEffect, State, Subscription>
) : Fragment() {

    private val recyclerValueEffect = Effect<List<Movie>>(emptyList())
    private val recyclerVisibilityEffect = Effect(Visibility.VISIBLE)
    private val progressBarEffect = Effect(Visibility.GONE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        frameLayout {
            recyclerView {
                size(Size.MATCH_PARENT, Size.MATCH_PARENT)
                layoutManager(GridLayoutManager(context, 2))
                adapter(MovieAdapter())
                recyclerValueEffect bind { (adapter as MovieAdapter).update(it) }
                recyclerVisibilityEffect bind { visibility(it) }
            }
            progressBar {
                size(Size.WRAP_CONTENT, Size.WRAP_CONTENT)
                visibility(Visibility.GONE)
                gravity(Gravity.CENTER)
                progressBarEffect bind { visibility(it) }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connector bindAction Action.LoadAction(1)
    }


    override fun onStart() {
        super.onStart()
        connector.connect(::render)
    }

    private fun render(state: State) {
        progressBarEffect.value = if (state.loading) Visibility.VISIBLE else Visibility.GONE
        state.data?.let { recyclerValueEffect.value = it }
        recyclerVisibilityEffect.value = if (state.loading) Visibility.GONE else Visibility.VISIBLE
        state.error?.let {
            progressBarEffect.value = Visibility.GONE
            recyclerVisibilityEffect.value = Visibility.GONE
        }
    }

    override fun onStop() {
        super.onStop()
        connector.disconnect()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        progressBarEffect.unbind()
        recyclerValueEffect.unbind()
        recyclerVisibilityEffect.unbind()
    }

    companion object {
        const val TAG = "MovieFragment"
    }
}
