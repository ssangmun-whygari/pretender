<template>
  <v-dialog v-model="modalActivated" max-width="1280">
    <template v-slot:activator="{ props: activatorProps }">
      <div class="ml-1 mr-1" v-if="logined == false" v-bind="activatorProps"><a href="javascript:void(0);">로그인</a></div>
    </template>

    <template v-slot:default="{ isActive }">
      <v-card>
        <v-row no-gutters>
          <!-- md 이상일 때만 보임 -->
          <v-col
            cols="6"
            class="d-none d-md-flex"
            style="background-color: #eee; align-items: center; justify-content: center;"
          >
            <!-- 3d shader -->
             <AbstractCirclePatternShader/>
          </v-col>

          <!-- 로그인 폼 -->
          <v-col cols="12" md="6">
            <v-card-title><div class="mt-10 ml-5 mr-5 members-form-title">로그인</div></v-card-title>
            <v-card-text class="card-text d-flex flex-column justify-center">
              <div class="error-message d-flex flex-column">
                <v-sheet rounded v-if="errorMessage.length > 0" height="48" class="d-flex flex-column justify-center bg-deep-orange-lighten-1">
                  <div class="error-message-text">{{ errorMessage }}</div>
                </v-sheet>
              </div>
              <div class="label mb-3">아이디</div>
              <v-text-field v-model="userId" class="pb-5" placeholder="pretender@email.co.kr" variant="outlined"></v-text-field>
              <div class="label mb-3">비밀번호</div>
              <v-text-field 
                v-model="userPassword" class="pb-5"  variant="outlined"
                :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
                :type="showPassword ? 'password' : 'text'"
                @click:append-inner="showPassword = !showPassword"
              ></v-text-field>
              <v-btn 
                class="mt-5" height="48" color="primary" block outlined 
                @click="async () => {let result = await requestAuth(); isActive.value = !result}">
                <div class="button-text">로그인하기</div>
              </v-btn>
              <v-btn 
                class="mt-5" height="48"  block outlined 
                @click="requestSocialLoginNaver">
                <div class="button-text">네이버로 로그인하기</div>
              </v-btn>
              <v-btn 
                class="mt-5" height="48"  block outlined 
                @click="requestSocialLoginKakao">
                <div class="button-text">카카오로 로그인하기</div>
              </v-btn>
            </v-card-text>
            <v-card-actions>
              <!-- <v-spacer />
              <v-btn text="Close Dialog" @click="isActive.value = false" /> -->
            </v-card-actions>
          </v-col>
        </v-row>
      </v-card>
    </template>
  </v-dialog>
</template>

<style scoped>
  .members-form-title {
    font-size: 32px;
    font-weight: bold;
  }
  
  .label {
    font-size: 16px;
  }

  .button-text {
    font-size: 16px;
  }

  .card-text {
    margin-left: 20px;
    margin-right: 20px;
    margin-bottom: 60px;
  }

  .error-message {
    height: 72px;
  }

  .error-message-text {
    font-size: 18px;
    padding-left: 12px;
    padding-right: 12px;
  }
</style>

<script setup>
import { useRouter, useRoute } from 'vue-router';
import { useNavigationStore } from '@/composables/stores/navigation';
import axios from 'axios';
import { ref, watch } from 'vue';
import AbstractCirclePatternShader from './AbstractCirclePatternShader.vue';
const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL
const router = useRouter();
const navigationStore = useNavigationStore();
const emit =  defineEmits(['requestCheckAuthenticated', 'requestRestoreTrigger'])

// 부모가 내려주는 props
const props = defineProps({
  triggered: {
    type: Boolean,
    default: false
  }
})

const modalActivated = ref(false)
// 부모에서 값 내려왔으면 내부 상태와 동기화
watch(
  () => props.triggered,
  (val) => {
    console.log("LoginModal에서 triggered props의 변화를 감지함")
    if (val === true) {
      modalActivated.value = val
      console.log(`modalActivated의 값이 ${val}로 변경됨`)
      emit("requestRestoreTrigger")
    }
  },
)

// 상태 변수
const logined = ref(false)
const userId = ref('');
const userPassword = ref('');
const showPassword = ref('false')
const errorMessage = ref('');


function requestCheckAuthenticated() {
  emit('requestCheckAuthenticated')
}

async function requestAuth() {
  try {
    console.log('requestAuth...');

    // 로그인 요청
    const response = await axios.get(apiBaseUrl + '/api/login', {
      auth: {
        username: userId.value,
        password: userPassword.value,
      },
      withCredentials: true,
      headers: {
        'X-Requested-With': 'XMLHttpRequest',
      },
    });

    // 로그인 성공 처리
    if (response.status === 200) {
      console.log('로그인 성공:', response);
      requestCheckAuthenticated()
      logined.value = true // 로그아웃을 통해서만 다시 로그인 가능
      errorMessage.value = ''
      return true
    } else {
      // 성공이지만 예상치 못한 상태
      errorMessage.value = '예상치 못한 오류가 발생했습니다.';
      return false
    }
  } catch (error) {
    console.error('로그인 실패:', error);
    // 오류 처리
    if (!error.response || error.response.status === 401) {
      errorMessage.value = '아이디나 비밀번호가 잘못되었습니다. 다시 확인해주세요.';
      return false
    } else {
      errorMessage.value = '로그인 중 오류가 발생했습니다.';
      return false
    }
  }
}

async function requestSocialLoginNaver() {
  window.location.href = apiBaseUrl + "/api/login/naver";
}

async function requestSocialLoginKakao() {
  window.location.href = apiBaseUrl + "/api/login/kakao";
}
</script>