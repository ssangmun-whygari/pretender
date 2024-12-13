import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useMediaDetailStore = defineStore('mediaDetail', () => {
  const id = ref(0)
  const backDropPath = ref('')
  const setMediaDetail = (obj) => {
    id.value = obj.id,
    backDropPath.value = obj.backDropPath
  }

  return  { id, backDropPath, setMediaDetail }
})