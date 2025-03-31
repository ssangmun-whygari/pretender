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
        @input="validateId"
      />
      <div v-if="hasTypedId && (!isIdValid || idErrorMessage)" class="error_msg ">
        {{ hasTypedId && !isIdValid ? '유효한 이메일 주소를 입력하세요.' : idErrorMessage }}
      </div>

      <!-- 비밀번호 -->
      비밀번호
      <v-text-field
        v-model="password"
        type="password"
        placeholder="6~15자 특수문자, 영어대문자, 영어소문자, 숫자 1자 이상 포함"
        outlined
        dense
        class="text-field"
        @input="validatePassword"
      />
      <div v-if="hasTypedPassword && !isPasswordValid" class="error_msg ">
        비밀번호는 6~15자, 영문 대소문자, 숫자, 특수문자를 포함해야 합니다.
      </div>

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
          :model-value="tempDate" 
          no-title
          @update:modelValue="handleDateInput"
          :min="minDate"
        />
      </v-menu>
      <div v-if="birthdateErrorMessage" class="error_msg ">
        {{ birthdateErrorMessage }}
      </div>

      <!-- 성별 -->
      성별
      <v-radio-group v-model="gender" row>
        <v-radio label="남성" value="M"></v-radio>
        <v-radio label="여성" value="F"></v-radio>
        <v-radio label="비공개" value="N"></v-radio>
      </v-radio-group>
      <div v-if="genderErrorMessage" class="error_msg ">
        {{ genderErrorMessage }}
      </div>

      <!-- 별명 -->
      닉네임
      <v-text-field
        v-model="nickname"
        outlined
        dense
        class="text-field"
      />
      <div v-if="nicknameErrorMessage" class="error_msg ">
        {{ nicknameErrorMessage }}
      </div>

      <v-btn class="button" color="primary" block outlined @click="handleSubmit">가입하기</v-btn>
    </v-card>
  </v-container>
</template>

<script>
import { defineComponent, ref, computed, watch } from 'vue';
import { useSignUpLogic } from './SignUpView.js'; 

export default defineComponent({
  setup() {
    return { ...useSignUpLogic() };
  },
});
</script>

<style src="./SignUpView.css"></style>

