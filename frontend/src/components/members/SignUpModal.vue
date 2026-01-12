<template>
  <v-dialog v-model="dialog" max-width="1280">
    <template v-slot:activator="{ props: activatorProps }">
      <div class="ml-1 mr-1" v-bind="activatorProps"><a href="javascript:void(0);">회원가입</a></div>
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

          <!-- 회원가입 폼 -->
          <v-col cols="12" md="6">
            <v-card-title><div class="mt-10 ml-5 mr-5 members-form-title">회원가입</div></v-card-title>
            <v-card-text class="card-text d-flex flex-column justify-center">
              <div class="label mb-3">아이디</div>
              <v-text-field v-model="userId" placeholder="pretender@email.co.kr" variant="outlined"></v-text-field>
              <div class="validation-message d-flex flex-column justify-center">
                <div class="not-valid">{{ emailErrorMessage }}</div>
              </div>
              <div class="label mb-3">비밀번호</div>
              <v-text-field 
                v-model="userPassword" variant="outlined"
                :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
                :type="showPassword ? 'password' : 'text'"
                @click:append-inner="showPassword = !showPassword"
              ></v-text-field>
              <div class="validation-message d-flex flex-column justify-center">
                <div :class="{ 'not-valid' : !isValidPassword,'valid' : isValidPassword}">
                  {{ passwordErrorMessage }}
                </div>
              </div>
              <div class="label mb-3">생년월일</div>
              <!-- 팝업 메뉴 -->
              <v-menu v-model="menuOpened" :close-on-content-click="false" :close-on>
                <template v-slot:activator="{ props }">
                  <v-text-field 
                    v-bind="props"
                    v-model="formattedDate" 
                    readonly
                    variant="outlined"
                  ></v-text-field>
                </template>
                <v-date-picker
                  v-model="selectedDate"
                  @update:modelValue="onDateSelected"
                  @update:month="onMonthChange"
                  @update:year="onYearChange"
                />
              </v-menu>
              <div class="validation-message d-flex flex-column justify-center">
                <div class="not-valid">{{ birthdateErrorMessage }}</div>
              </div>

              <div class="label">성별</div>
              <v-radio-group class="mb-3" v-model="gender" inline>
                <v-radio class="ml-3 mr-3" label="남성" value="M"></v-radio>
                <v-radio class="ml-3 mr-3" label="여성" value="F"></v-radio>
                <v-radio class="ml-3 mr-3" label="비공개" value="N"></v-radio>
              </v-radio-group>
              <div class="validation-message d-flex flex-column justify-center">
                <div class="not-valid">{{ genderErrorMessage }}</div>
              </div>


              <div class="label">닉네임</div>
              <v-text-field 
                v-model="nickname"  variant="outlined"
              ></v-text-field>
              <div class="validation-message d-flex flex-column justify-center">
                <div class="not-valid">{{ nicknameErrorMessage }}</div>
              </div>


              <v-btn 
                class="mt-5" height="48" color="primary" block outlined 
                @click="handleSubmit">
                <div class="button-text">가입하기</div>
              </v-btn>


            </v-card-text>
            <v-card-actions>
            </v-card-actions>
          </v-col>
        </v-row>
      </v-card>
    </template>
  </v-dialog>
</template>

<style scoped>
  .button-text {
    font-size: 16px;
  }

  .members-form-title {
    font-size: 32px;
    font-weight: bold;
  }

  .label {
    font-size: 16px;
  }

  .card-text {
    margin-left: 20px;
    margin-right: 20px;
    margin-bottom: 60px;
  }

  .validation-message {
    height: 32px;
  }

  .not-valid {
    color: red;
  }

  .valid {
    color: green;
  }
</style>

<script setup>
  import axios from 'axios'
  import { ref, watch, onMounted, nextTick } from 'vue'
  const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL
  const dialog = ref(null)

  const menuOpened = ref(null)
  const selectedDate = ref(null)
  const formattedDate = ref(null)
  const koreanYoils = ['일', '월', '화', '수', '목', '금', '토']
  const monthTable = {
    'Jan' : 1, 'Feb': 2, 'Mar' : 3, 'Apr' : 4, 'May' : 5, 'Jun' : 6, 'Jul' : 7,
    'Aug' : 8, 'Sep' : 9, 'Oct' : 10, 'Nov' : 11, 'Dec' : 12
  }
  // const lastEngYearAndMonth = ref(null)

  watch(menuOpened, (opened) => {
    if (opened === true) {
      nextTick(() => {
        let titleElement = document.querySelector(".v-picker-title")
        console.log(titleElement)
        titleElement.style.display = 'none'
        let infoElement = document.querySelector(".v-date-picker-header__content")
        infoElement.innerText = '날짜를 선택해주세요'
        let monthButton = document.querySelector('[data-testid="month-btn"]')
        let prevMonthButton = document.querySelector('[data-testid="prev-month"]')
        let nextMonthButton = document.querySelector('[data-testid="next-month"]')
        monthButton.addEventListener('click', translateYoil)
        // prevMonthButton.addEventListener('click', translateYoil)
        // nextMonthButton.addEventListener('click', translateYoil)
        translateYoil()
        translateYearMonthButton(monthButton)
      })
    }
  })

  function onMonthChange(month) {
    // console.log("monthChanged!!! : " + month)
    let button = document.querySelector('[data-testid="month-btn"]')
    let element = button.querySelector('.v-btn__content')
    let words = element.textContent.split(" ") // 예) 2025년 8월
    words[1] = parseInt(month) + 1 + "월"
    let newText = words.join(" ")
    // console.log("newText : " + newText)
    element.textContent = newText
    nextTick(() => {
      translateYoil()
    })
  }

  function onYearChange(year) {
    // console.log("yearChanged!!! : " + year)
    let button = document.querySelector('[data-testid="month-btn"]')
    let element = button.querySelector('.v-btn__content')
    let words = element.textContent.split(" ") // 예) 2025년 8월
    words[0] = parseInt(year) +  "년"
    let newText = words.join(" ")
    // console.log("newText : " + newText)
    element.textContent = newText
    nextTick(() => {
      translateYoil()
    })
  }

  function translateYearMonthButton(button) {
    let element = button.querySelector('.v-btn__content')
    let string = element.textContent
    // lastEngYearAndMonth.value = string
    let words = string.split(" ")
    let month = String(monthTable[words[0].slice(0, 3)])
    let year = words[1]
    element.textContent = `${year}년 ${month}월`
  }

  function translateYoil() {
    // 요일 번역
    let yoil = document.querySelectorAll('.v-date-picker-month__weekday');
    let start = 0;
    for (let i = 0; i < yoil.length; i++) {
      yoil[i].innerText = koreanYoils[i % koreanYoils.length]
    }
  }

  function onDateSelected() {
    let date = selectedDate.value
    let year = date.getFullYear();
    let month = String(date.getMonth() + 1).padStart(2, '0')
    let day = String(date.getDate()).padStart(2, '0')
    formattedDate.value = `${year}-${month}-${day}`
    menuOpened.value = false
  }

  const userId = ref('')
  const userPassword = ref('')
  const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]{6,15}$/;
  const isValidPassword = ref(false)
  const gender = ref(null)
  const nickname = ref('')

  // 입력이 변할때만 유효성 체크
  watch(userPassword, (password) => {
    setPasswordErrorMessage()
  })
  watch(userId, (id) => {
    setEmailErrorMessage()
  })
  watch(formattedDate, (string) => {
    setBirthdayErrorMessage()
  })
  watch(gender, (g) => {
    setGenderErrorMessage()
  })
  watch(nickname, (n) => {
    setNicknameErrorMessage()
  })

  let passwordErrorMessage = ref('')
  function setPasswordErrorMessage() {
    if (!PASSWORD_REGEX.test(userPassword.value)) {
      passwordErrorMessage.value = "⚠️ 6~15자, 영문 대소문자, 숫자, 특수문자를 포함"
      isValidPassword.value = false
      return false
    } else {
      passwordErrorMessage.value = "✅ 6~15자, 영문 대소문자, 숫자, 특수문자를 포함"
      isValidPassword.value = true
      return true
    }
  }

  async function handleSubmit() {
    let isEmailValid = setEmailErrorMessage()
    let isPasswordValid = setPasswordErrorMessage()
    let isBirthdayValid = setBirthdayErrorMessage()
    let isGenderValid = setGenderErrorMessage()
    let isNicknameValid = setNicknameErrorMessage()
    console.log("회원가입 시도")
    if (!isEmailValid || !isPasswordValid || !isBirthdayValid || !isGenderValid || !isNicknameValid) {
      return
    }

    const personalInfo = {
      id: userId.value,
      password: userPassword.value,
      nickname: nickname.value,
      gender: gender.value,
      birthday: formattedDate.value.replace(/-/g, ""),
    };

    try {
      const response = await axios.post(apiBaseUrl + '/api/signup', personalInfo);
      console.log('Response', response.data);
    } catch (error) {
      console.error('Error:', error.response?.data || error.message);
      if (error.response && error.response.status === 400){
        const errorBody = error.response.data;
        // 서버에서 받아온 메시지 화면에 표시
        if (errorBody["result"] === "fail" && errorBody["errorCode"] == "memberId") {
          emailErrorMessage.value = "⚠️" + errorBody["message"]
        } else if (errorBody["result"] === "fail" && errorBody["errorCode"] == "memberPassword") {
          passwordErrorMessage.value = "⚠️" + errorBody["message"]
        } else if (rrorBody["result"] === "fail" && errorBody["errorCode"] == "nickname") {
          nicknameErrorMessage.value = "⚠️" + errorBody["message"]
        }
      } else {
        console.log('오류:', error.message);
      }
    }
    // 회원 가입 성공, 모달 닫기
    clearForm()
    dialog.value = false
  }

  function clearForm() {
    userId.value = ""
    userPassword.value = ""
    nickname.value = ""
    formattedDate.value = ""
    emailErrorMessage.value = ""
    nicknameErrorMessage.value = ""
    birthdateErrorMessage.value = ""
    genderErrorMessage.value = ""


  }

  const emailErrorMessage = ref('')
  function setEmailErrorMessage() {
    if (!EMAIL_REGEX.test(userId.value)) {
      emailErrorMessage.value = "⚠️ 유효한 이메일 주소를 입력하세요"
      return false
    } else {
      emailErrorMessage.value = ""
      return true
    }
  }


  const nicknameErrorMessage = ref('')
  function setNicknameErrorMessage() {
    if (!nickname.value || nickname.value.length <= 0) {
      nicknameErrorMessage.value = "⚠️ 닉네임을 설정해주세요"
      return false
    } else if (nickname.value.length > 15) {
      nicknameErrorMessage.value = "⚠️ 닉네임은 15자를 넘길 수 없습니다."
      return false
    }
    nicknameErrorMessage.value =""
    return true
  }
  const birthdateErrorMessage = ref('')
  function setBirthdayErrorMessage() { // false 반환시 생일입력에 문제가 있음
    if (formattedDate.value && formattedDate.value.length > 0) {
      birthdateErrorMessage.value = ""
      return true
    } else {
      birthdateErrorMessage.value = "⚠️ 생년월일을 입력하세요"
      return false
    }
  }
  const genderErrorMessage = ref('')
  function setGenderErrorMessage() {
    if (gender.value) {
      genderErrorMessage.value = ""
      return true
    } else {
      genderErrorMessage.value = "⚠️ 성별을 입력하세요"
      return false
    }
  }


</script>