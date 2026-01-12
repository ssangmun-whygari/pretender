<template>
  <AppHeader 
    :loginModalTriggered="loginModalTriggered"
    @requestRestoreTrigger="executeRestoreTrigger"
  ></AppHeader>
  <!-- 자식에서 "requestOpenLoginModal"라는 emit이 오면 자신 안에 정의된 requestOpenLoginModal() 함수로 보냄 -->
  <MediaDetail 
    @requestOpenLoginModal="requestOpenLoginModel"
    v-model:voteChartDialog="voteChartDialog"
  />
  <LikePerCharacterGraphModal v-model:voteChartDialog="voteChartDialog" :media_id="id", :media_type="type"/>
</template>

<script setup>
  import AppHeader from '../AppHeader.vue';
  import { useRoute } from 'vue-router';
  import { useNavigationStore } from '@/composables/stores/navigation';
  import { ref, watch } from 'vue'
  const loginModalTriggered = ref(false)
  const voteChartDialog = ref({character_id: null, show: false}) // true이면 캐릭터별 차트 다이얼로그가 열려있음
  const id = ref(useRoute().query.id) // 주소창에서 id 쿼리를 얻어옴
  const type = ref(useRoute().query.type)

  function executeRestoreTrigger() {
    console.log("DetailView의 자식 컴포넌트로부터 requestRestoreTrigger 요청을 받음")
    console.log("DetailView의 loginModalTriggered가 false로 복원됨")
    loginModalTriggered.value = false
  }

  function requestOpenLoginModel() {
    console.log("DetailView가 자식 컴포넌트 AppHeader에게 loginModalTriggered를 내려줌")
    loginModalTriggered.value = true
  }

  // 방문할 때마다 previousPage 초기화
  let navigationStore = useNavigationStore()
  navigationStore.setPreviousPage('/')
</script>