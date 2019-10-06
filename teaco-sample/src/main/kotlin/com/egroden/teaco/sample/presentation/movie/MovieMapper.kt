package com.egroden.teaco.sample.presentation.movie

import com.egroden.teaco.sample.data.dto.MovieDto

object MovieMapper {
    fun toModel(dto: MovieDto): MovieModel =
        MovieModel(
            id = dto.id,
            voteCount = dto.voteCount,
            title = dto.title,
            originalTitle = dto.originalTitle,
            overview = dto.overview,
            voteAverage = dto.voteAverage,
            backdropPath = dto.backdropPath,
            releaseDate = dto.releaseDate,
            rating = dto.rating
        )

    fun toDto(model: MovieModel): MovieDto =
        MovieDto(
            id = model.id,
            voteCount = model.voteCount,
            title = model.title,
            originalTitle = model.originalTitle,
            overview = model.overview,
            voteAverage = model.voteAverage,
            backdropPath = model.backdropPath,
            releaseDate = model.releaseDate,
            rating = model.rating
        )
}