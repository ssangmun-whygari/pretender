import { defineStore } from 'pinia';
import { ref } from'vue';

export const usePopularMoviesStore = defineStore('popularMovies', () => {
  let popularMovieInfos = ref([])

  const setPopularMovieInfos = (array) => {
    if (popularMovieInfos.value.length > 0) {
      popularMovieInfos.value = array
    } else {
      popularMovieInfos.value = popularMovieInfos.value.concat(array)
    }
  };

  const getPopularMovieInfos = () => {
    return popularMovieInfos // ref 객체 반환
  }

  return {
    getPopularMovieInfos,
    setPopularMovieInfos
  }
})