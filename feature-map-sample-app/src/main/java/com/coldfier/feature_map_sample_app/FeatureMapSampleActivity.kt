package com.coldfier.feature_map_sample_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.coldfier.core_utils.di.DepsMap
import com.coldfier.core_utils.di.HasDependencies
import com.coldfier.feature_map.MapDeps
import com.coldfier.feature_map.ui.MapFragment

class FeatureMapSampleActivity : AppCompatActivity(), HasDependencies {

    override val depsMap: DepsMap = mapOf(MapDeps::class.java to object : MapDeps {})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feature_map_sample)

        supportFragmentManager.commit {
            replace(R.id.layout_container, MapFragment::class.java, null)
            setReorderingAllowed(true)
        }
    }
}