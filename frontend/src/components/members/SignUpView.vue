<template>
  <v-container class="d-flex flex-column justify-center align-center" style="height: 100%;">
    <v-card class="pa-4" elevation="2" style="max-width: 400px; width: 100%;">
      <v-card-title class="text-h6 font-weight-bold text-center">
        회원 가입
      </v-card-title>

      <!-- 아이디 -->
      아이디
      <v-text-field
        placeholder="id@email.com"
        outlined
        dense
        class="mt-3"
      ></v-text-field>

      <!-- 비밀번호 -->
      비밀번호
      <v-text-field
        type="password"
        placeholder="8-16자리/영문 대소문자, 숫자, 특수문자 조합"
        outlined
        dense
      ></v-text-field>

      <!-- 생년월일 -->
      생년월일
       <v-dialog v-model="dateDialog" max-width="400px">
        <template v-slot:activator="{ on, attrs }">
          <v-text-field
            v-model="birthDate"
            label="YYYY-MM-DD"
            readonly
            outlined
            dense
            v-bind="attrs"
            v-on="on"
          ></v-text-field>
        </template>
        <v-card>
          <v-card-title>
            생년월일 선택
          </v-card-title>
          <v-card-text>
            <v-calendar
              v-model="birthDate"
              type="date"
              locale="ko-KR"
            ></v-calendar>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn text @click="dateDialog = false">취소</v-btn>
            <v-btn text @click="dateDialog = false">확인</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog> 
      <template>
  <v-row class="fill-height">
    <v-col>
      <v-sheet height="600">
        <v-calendar
          ref="calendar"
          v-model="today"
          :events="events"
          color="primary"
          type="month"
        ></v-calendar>
      </v-sheet>
    </v-col>
  </v-row>
</template>

      <!-- 성별 -->
      성별
      <v-radio-group v-model="gender" row>
        <v-radio label="남성" value="male"></v-radio>
        <v-radio label="여성" value="female"></v-radio>
      </v-radio-group>

      <!-- 별명 -->
      별명
      <v-text-field
        placeholder="사용할 별명을 입력하세요"
        outlined
        dense
      ></v-text-field>

      <v-btn class="mt-2" color="primary" block outlined>가입하기</v-btn>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref } from 'vue';
import { VCalendar } from 'vuetify/labs/VCalendar';

const birthDate = ref('');
const dateDialog = ref(false);
const gender = ref('');
</script>

<script>
import { createVuetify } from 'vuetify';
import { VCalendar } from 'vuetify/labs/VCalendar';

export default createVuetify({
  components: {
    VCalendar,
  },
});
</script>
