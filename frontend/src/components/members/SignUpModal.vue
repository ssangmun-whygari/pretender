<template>
  <v-dialog max-width="1280">
    <template v-slot:activator="{ props: activatorProps }">
      <div class="ml-1 mr-1" v-bind="activatorProps">회원가입</div>
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
              <v-text-field v-model="userId" class="pb-5" placeholder="pretender@email.co.kr" variant="outlined"></v-text-field>
              <div class="label mb-3">비밀번호</div>
              <v-text-field 
                v-model="userPassword" class="pb-5"  variant="outlined"
                :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
                :type="showPassword ? 'password' : 'text'"
                @click:append-inner="showPassword = !showPassword"
              ></v-text-field>
              <div class="validation-message d-flex flex-column justify-center">
                <div 
                  :class="{ 
                    'not-valid' : !isValidPassword,
                    'valid' : isValidPassword
                  }"
                  v-if="userPassword.length > 0">{{ getPasswordRequirement() }}
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
                    class="pb-5"  variant="outlined"
                  ></v-text-field>
                </template>
                <v-date-picker
                  v-model="selectedDate"
                  @update:modelValue="onDateSelected"
                  @update:month="onMonthChange"
                  @update:year="onYearChange"
                />
              </v-menu>





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
    height: 100px;
  }

  .not-valid {
    color: red;
  }

  .valid {
    color: green;
  }
</style>

<script setup>
  import { ref, watch, onMounted, nextTick } from 'vue'

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



  const userPassword = ref('')
  const isValidPassword = ref(false)
  const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]{6,15}$/;

  // 입력이 변할때만 유효성 체크
  watch(userPassword, (password) => {
    isValidPassword.value = PASSWORD_REGEX.test(password);
  })

  function getPasswordRequirement() {
    if (isValidPassword.value === true) {
      return "✅ 6~15자, 영문 대소문자, 숫자, 특수문자를 포함"
    } else {
      return "⚠️ 6~15자, 영문 대소문자, 숫자, 특수문자를 포함"
    }
  }

</script>