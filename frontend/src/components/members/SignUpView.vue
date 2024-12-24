<template>
  <v-container class="container">
    <v-card class="card" elevation="2">
      <v-card-title class="card-title">
        회원 가입
      </v-card-title>

      <!-- 아이디 -->
       아이디
      <v-text-field
        v-model="id"
        placeholder="pretender@email.co.kr"
        outlined
        dense
        class="text-field"
        :error="hasTypedId && (!isIdValid || !!idErrorMessage)"
        :error-messages="hasTypedId && !isIdValid ? '유효한 이메일 주소를 입력하세요.' : idErrorMessage"
        @input="validateId"
      />

      <!-- 비밀번호 -->
       비밀번호
      <v-text-field
        v-model="password"
        type="password"
        placeholder="6~15자 특수문자, 영어대문자, 영어소문자, 숫자 1자 이상 포함"
        outlined
        dense
        class="text-field"
        :error="hasTypedPassword && !isPasswordValid"
        :error-messages="hasTypedPassword && !isPasswordValid ? '비밀번호는 6~15자, 영문 대소문자, 숫자, 특수문자를 포함해야 합니다.' : ''"
        @input="validatePassword"
      />

      <!-- 생년월일 -->
       생년월일
      <v-menu
        v-model="fromDateMenu"
        :close-on-content-click="false"
        :nudge-right="40"
        transition="scale-transition"
        offset-y
        max-width="290px"
        min-width="290px"
      >
        <template v-slot:activator="{ props }">
          <v-text-field
            placeholder="생년월일"
            prepend-inner-icon="mdi-calendar"
            readonly
            :value="fromDateDisp"
            v-bind="props"
            outlined
            dense
            class="text-field"
          ></v-text-field>
        </template>
        <v-date-picker
          locale="ko-KR"
          v-model="fromDateVal"
          no-title
          @update:modelValue="handleDateInput"
          :min="minDate"
        ></v-date-picker>
      </v-menu>

      <!-- 성별 -->
      성별
      <v-radio-group v-model="gender" row>
        <v-radio label="남성" value="M"></v-radio>
        <v-radio label="여성" value="F"></v-radio>
        <v-radio label="비공개" value="N"></v-radio>
      </v-radio-group>

      <!-- 별명 -->
       닉네임
      <v-text-field
        v-model="nickname"
        outlined
        dense
        class="text-field"
        :error="!!nicknameErrorMessage"
        :error-messages="nicknameErrorMessage"
      />

      <v-btn class="button" color="primary" block outlined @click="handleSubmit">가입하기</v-btn>
    </v-card>
  </v-container>
</template>

<script>
import { defineComponent } from 'vue';
import { useSignUpLogic } from './SignUpView.js'; // 외부 스크립트 가져오기

export default defineComponent({
  setup() {
    // 외부 함수 호출
    return { ...useSignUpLogic() };
  },
});
</script>

<style src="./SignUpView.css"></style>