import { onErrorCaptured, ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
// deprecated : 내가 원인을 잘못 파악한 것일수도 굳이 필요없을지도

// vue 라우터는 라우팅을 한 경로를 방문했을 때 에러가 발생하면 전 경로의 컴포넌트를 언마우팅하지 않음
// 따라서 뒤로가기 버튼을 눌러서 전 페이지로 이동시 mounted()가 호출이 안됨
// 그래서 에러가 발생해도 mounted()가 호출될 수 있게 하기 위한 store이다.
// 심각하지 않은 에러일때 : 뒤로가기 버튼을 누르면 바로 원래 주소로 보낸다.
// 심각한 에러일때 : 에러가 발생하면 먼저 에러 페이지를 보여준다. 뒤로가기 버튼을 누르면 에러가 발생하지 않은 페이지로 보내준다.
// (state의 pop을 2번 해야 할 수 있음, 일단 아직 구현되지 않음)
export function useErrorBackNavigation() {
  const hasMinorError = ref(false)
  const hasNoError = ref(true)
  const router = useRouter()
  const route = useRoute()


  // 에러 분류 함수
  function classifyError(err) {
    if (err?.severity === 'minor') return 'minor'
    if (err?.isAxiosError || err?.name === 'AxiosError') return 'minor'
    return 'critical'
  }

  // 에러 감지
  onErrorCaptured((err) => {
    console.error('Captured error:', err)
    console.error('에러가 발생한 페이지 : ' + '/')
    const severity = classifyError(err)
    if (severity === 'minor') {
      hasMinorError.value = true
      hasNoError.value = false
    } else {
      // TODO : 심각한 에러 발생시
      hasNoError.value = false
    }
    return false // 전파 막기
  })

  let unregister
  onMounted(() => {
    unregister = router.afterEach((to, from, failure) => {
      console.log('navigated:', from.fullPath, '→', to.fullPath)
      // from,fullPath가 뒤로가기 주소를 요청하는 주소임
      if (hasNoError.value === true || hasMinorError.value === true) {
        console.log("작동중...")
        router.replace(to.value)
      }
    })
  })

  onBeforeUnmount(() => {
    if (unregister) unregister()
  })
}