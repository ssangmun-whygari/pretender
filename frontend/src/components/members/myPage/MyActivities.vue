<template>
  <v-row justify="center">
    <v-col lg="8" cols="12">
      <h1 class="mt-3 mb-3">내가 남긴 리뷰/댓글</h1>
      <v-text-field
        class="w-100"
        variant="outlined"
        append-inner-icon="mdi-magnify"
        density="compact"
        placeholder="리뷰/댓글을 남긴 작품 제목을 입력하세요..."
        v-on:keyup.enter="onEnter"
        v-model="word"
      ></v-text-field>
    <!---->

      <!-- 검색 결과가 있는 경우 -->
      <template v-if="isSearching ? searchActResults.length : myActivitiesData.length">
        <v-list>
          <v-list-item v-for="activity in (isSearching ? searchActResults : myActivitiesData)" :key="activity.no" lines="two">
            <a @click.prevent="goToComment(activity)">
              <v-list-item-title class="title_style" v-html="activity.content.replace(/\\n/g, '<br>')"></v-list-item-title>
              <v-list-item-subtitle v-html="getSubtitle(activity)"></v-list-item-subtitle> 
            </a>
            <v-divider class="border-opacity-50"></v-divider>
          </v-list-item>
        </v-list>
      </template>
      
      <!-- 검색 결과 없음 -->
      <template v-else>
        <span class="background">
          <p class="noActivities">{{ isSearching ? "검색 결과가 없습니다." : "내 활동이 없습니다." }}</p>
        </span>
      </template>
  
 
    </v-col>
  </v-row>
  <v-pagination 
    v-model="currentPage"
    :length="totalPages"
    @click="(event) => console.log('페이지 클릭됨:', event)"
    @update:modelValue="changePages"
  />
</template>
<script setup>
import { onMounted, ref, watch } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();
const myActivitiesData =ref([]);
const searchActResults = ref([]);
const word =ref('');
const totalPages = ref(0);
const currentPage = ref(1);
const isSearching = ref(false);

const getSubtitle = (activity) => {
  if (activity.correctDate) {
    return `<span class="subtitle_span"> ${activity.mediaTitle} / ${formatDate(activity.correctDate)} 수정됨</span>`;
  } else {
    return `<span class="subtitle_span"> ${activity.mediaTitle} / ${formatDate(activity.postDate)} 작성</span>`;
  }
};

async function checkAuthenticated() {
  try {
    let response = await axios.get('http://localhost:8080/api/authenticated', {
      withCredentials: true,
      headers: {
        "X-Requested-With": "XMLHttpRequest"},
    });
    console.log("✅ 인증 체크 완료:", response);
    if(!response.data ||response.data ===''){
      alert("로그인해주세요!");
      router.push('/login');
      return false;
    }
    return response.data && response.data.authenticated === true;
  } catch (err) {
    console.log("로그인 오류",err);
    if(err.response && err.response.status === 401){
    alert("로그인해주세요!");
    router.push('/login');
  }
    return false;
  }
}

  const fetchMyActivities = async (page = 0) => {
    try {
      let response = await axios.get("http://localhost:8080/api/members/myActivites", {
        params:{ page },
        withCredentials: true
      });
      myActivitiesData.value = response.data.myActivities;
      console.log(myActivitiesData.value);
      totalPages.value = response.data.totalPages || 0;
      currentPage.value = page + 1;
      console.log("totalPages: "+ totalPages.value);
      
    } catch (error) {
      console.log("fetchMyActivities에서 오류가 발생했습니다.");
    }
  }

  const searchMyActivities = async(page = 0) => {
    if(!word.value.trim()){
      alert("한글자 이상 입력해주세요");
    }
    try {
      let response = await axios.get("http://localhost:8080/api/members/searchMyAct",{
        params:{ page , word: word.value },
        withCredentials: true
      });
      searchActResults.value = response.data.searchMyActResults;
      totalPages.value = response.data.totalPages || 0;
      currentPage.value = page + 1;
      isSearching.value = true;  
      console.log("isSearching : "+ isSearching.value);
      console.log("!!!"+searchActResults.value.length);
    } catch (error) {
      console.log("searchMyAvtivities() in Error");
    }
  }

  const onEnter = async () => {
    currentPage.value = 1;
    searchMyActivities();
  }

    // 검색어 변경 시 자동 검색
    watch(word, () => {
      if (!word.value.trim()) {
        isSearching.value = false;
        fetchMyActivities();
      }
    });

  const goToComment = (activity) => {
    let targetUrl = `/detail?id=${activity.mediaId}&type=${activity.mediaType}`;

    if (activity.parentNo === 0) {
      targetUrl += `&comment=${activity.no}`;
    } else {
      targetUrl += `&comment=${activity.parentNo}&reply=${activity.no}`;
    }

    router.push(targetUrl);
  }

  // 날짜 포맷 함수
const formatDate = (date) => {
  if (!date) return "";

  const now = new Date();
  const diff = Math.floor((now - new Date(date)) / 1000); // 차이를 초 단위로 계산

  if (diff < 60) return `${diff}초 전`; // 1분 미만
  if (diff < 3600) return `${Math.floor(diff / 60)}분 전`; // 1시간 미만
  if (diff < 86400) return `${Math.floor(diff / 3600)}시간 전`; // 24시간 미만
  if (diff < 30 * 86400) return `${Math.floor(diff / 86400)}일 전`; // 30일 미만
  if (diff < 365 * 86400) return `${Math.floor(diff / (30 * 86400))}개월 전`; // 1년 미만
  return `${Math.floor(diff / (365 * 86400))}년 전`; // 1년 이상
};

const changePages = (page) => {
  console.log("현재페이지"+currentPage.value);
  currentPage.value = page; 
  if(isSearching.value){
    searchMyActivities(page - 1);
  }else{
    fetchMyActivities(page - 1); 
  }
};

  onMounted(async() =>{
    const isAuthenticated = await checkAuthenticated();

    if (isAuthenticated) await fetchMyActivities();
  
  
  })
</script>

<style>
  .v-input__details {
    display: none;
  }

  .title_style{
    color: #2c2b2b;
  }
  .subtitle_span{
    color: #888;
  }

  .background{
    display: flex;
    align-items: center;
    justify-content: center;
    height: 50vh;
    text-align: center;
    width: 100%;
  }

  .noActivities {
    color: #2c2b2b;
  }
</style>
