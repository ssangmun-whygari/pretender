import { ref, computed, watch } from 'vue';
import { useNavigationStore } from '../../composables/stores/navigation';
import { useRouter, useRoute } from 'vue-router';
import axios from 'axios';

export function useSignUpLogic() {
    const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL

    // Pinia, Router, Route 초기화
    const router = useRouter();
    const route = useRoute();
    const navigationStore = useNavigationStore();

    // 변수
    const id = ref('');
    const password = ref('');
    const nickname = ref('');
    const gender = ref('');
    const fromDateMenu = ref(false);
    const fromDateVal = ref(null); // Date 객체
    const defaultDate = new Date(1995, 0, 1);
    const minDate = ref(new Date(1900, 0, 1)); // 최소 날짜
    const tempDate =ref(null);

    // 검증
    const hasTypedId = ref(false);
    const hasTypedPassword = ref(false);
    const isIdValid = ref(true);
    const isPasswordValid = ref(true);
    const idErrorMessage = ref('');
    const nicknameErrorMessage = ref('');


    // 정규식
    const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]{6,15}$/;


  // 검증 함수
    function validateId() {
      hasTypedId.value = true;
      if (id.value === '') {
        isIdValid.value = true;
        idErrorMessage.value = '';
        return;
      }
      isIdValid.value = EMAIL_REGEX.test(id.value);
      idErrorMessage.value = isIdValid.value ? '' : '유효한 이메일 주소를 입력하세요';
    }

    function validatePassword() {
      hasTypedPassword.value = true;
      if (password.value === '') {
        isPasswordValid.value = true;
        return;
      }
      isPasswordValid.value = PASSWORD_REGEX.test(password.value);
    }

    // 생년월일
    const fromDateDisp = computed(() => {
      if (!fromDateVal.value) return '';
      const date = fromDateVal.value;
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}${month}${day}`;
    });


    function handleDateInput(date) {
      if (date instanceof Date) {
      fromDateVal.value = date; // 최종 선택 날짜
      }
      fromDateMenu.value = false; // 날짜 선택시 달력 닫기
    }

      // 달력 열릴 때 기본 날짜 표시
    watch(fromDateMenu, (isOpen) => {
      if (isOpen) {
        tempDate.value = fromDateVal.value || defaultDate; // 달력 열릴 때 기본 날짜 설정
      }
    });

    // 별명 자동 설정
    watch(id, (newId) => {
      const emailPrefix = newId.split('@')[0] || '';
      nickname.value = emailPrefix;
    });

    // 폼 제출
    async function handleSubmit() {

      idErrorMessage.value = '';
      nicknameErrorMessage.value = '';
      
      validateId();
      validatePassword();

      if (!isIdValid.value || !isPasswordValid.value) {
        console.error('아이디와 패스워드를 점검하세요');
        return;
      }

      const personalInfo = {
        id: id.value,
        password: password.value,
        nickname: nickname.value,
        gender: gender.value,
        birthday: fromDateDisp.value,
    };

    try{
      const response = await axios.post(apiBaseUrl + '/api/signup', personalInfo);
          console.log('Response', response.data);
          const previousPage = navigationStore.previousPage || '/';
          router.push(previousPage);
      
    }catch(error) {
          console.error('Error:', error.response?.data || error.message);
          // 서버에서 반환된 메시지 처리
          if(error.response && error.response.status === 400){
            const errorMessage = error.response.data;
            if(errorMessage.includes('가입한 회원')) {
              idErrorMessage.value = errorMessage;
            } else if (errorMessage.includes('사용중인 닉네임')){
              nicknameErrorMessage.value = errorMessage;
            }
          }else {
            console.log('오류:',error.message);
          }
      }

    }


    return {
      id,
      password,
      nickname,
      gender,
      fromDateMenu,
      fromDateVal,
      defaultDate,
      minDate,
      tempDate,
      hasTypedId,
      hasTypedPassword,
      isIdValid,
      isPasswordValid,
      idErrorMessage,
      nicknameErrorMessage,
      validateId,
      validatePassword,
      fromDateDisp,
      handleDateInput,
      handleSubmit,
    };
  }

