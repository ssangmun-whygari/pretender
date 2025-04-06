import { ref } from 'vue'
import { useRouter } from 'vue-router'

const transitionState = ref('')

export function usePageTransition() {
  const router = useRouter()

  function trigger(pathInfo) {
    transitionState.value = 'transition-enter'
    
    setTimeout(() => {
      router.push(pathInfo)
      transitionState.value = 'transition-leave'
    }, 1000)
  }

  function onEnd() {
    if (transitionState.value === 'transition-leave') {
      transitionState.value = ''
    }
  }

  return {
    transitionState,
    trigger,
    onEnd
  }
}