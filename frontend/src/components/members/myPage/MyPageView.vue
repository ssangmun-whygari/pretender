<template>
    <AppHeader></AppHeader>
    <!-- <h1>마이페이지</h1>
    <h2>환영합니다. {{ userId }} 님</h2> -->
    <v-container fluid>
      <v-row justify="center">
        <v-col id="profile-section" lg="8" cols="12">
          <div id="profile-backdrop" class="position-relative bg-blue-darken-4 text-black">
            <div id="profile-contents" class="d-flex position-absolute">
              <div id="profile-image-frame" class="position-relative">
                <div id="profile-image" class="w-100 h-100">
                  <img class="w-100 h-100" src="http://localhost:8080/api/members/profile/image"/>
                </div>
                <div id="profile-image-change-btn" class="position-absolute">
                  <v-icon icon="mdi-camera-image" size="large" @click="openUpdateProfileSection"/>
                </div>
              </div>
              <h2 class="align-self-end ml-3 mb-3">쌍문동 왜가리</h2>
            </div>
          </div>
        </v-col>
      </v-row>
      <v-row justify="center" id="image-upload-card-container">
        <v-col lg="8" cols="12">
          <v-card id="image-upload-card" :class="imageUploadCardStyle + ' pa-5'">
            <v-row>
              <v-col class="d-flex justify-space-between pb-0" cols="12">
                <h2>🔍 미리보기</h2>
                <v-icon icon="mdi-window-close" size="large" @click="closeUpdateProfileSection"/>
              </v-col>
              <v-col class="d-flex flex-column justify-center ga-1" sm="6" lg="4" cols="12">
                <img id="profile-image-editing" src="http://localhost:8080/api/members/profile/image"/>
              </v-col>
              <v-col class="d-flex justify-space-between flex-column" sm="6" lg="4" cols="12">
                <div class="d-flex flex-column ga-3">
                  <v-card class="bg-grey-lighten-2 pa-3">
                    <div>- 1:1 비율의 이미지를 추천해요</div>
                    <div>- 1MB 이하의 이미지를 업로드해주세요</div>
                  </v-card>
                  <!--reference : https://codepen.io/blachocolat/pen/BgMKRQ -->
                  <form enctype="multipart/form-data">
                    <v-btn class="w-100" color="primary" @click="onClickImageUploadBtn">파일 찾아보기...</v-btn>
                    <input id="image-upload-input" class="d-none" type="file" accept="image/*" @change="onChangeImageUploadInput">
                  </form>
                </div>
                <div class="d-flex flex-column ga-3">
                  <v-row v-if="successUpload == 'before'">
                    <v-col cols="6"><v-btn class="w-100 text-primary" @click="onClickSubmitImage">업로드</v-btn></v-col>
                    <v-col cols="6"><v-btn class="w-100 text-error" @click="closeUpdateProfileSection">취소</v-btn></v-col>
                  </v-row>
                  <v-row v-if="successUpload == 'complete'">
                    <v-col cols="12"><v-btn class="w-100 text-green" @click="closeUpdateProfileSection">확인</v-btn></v-col>
                  </v-row>
                  <div style="min-height: 2.0rem;">
                    <v-card :class="imageUploadMessageCardClass" :style="imageUploadMessageCardStyle">
                      <div id="image-upload-message">{{ imageUploadMessage }}</div>
                    </v-card>
                  </div>
                </div>
              </v-col>
            </v-row>
          </v-card>
          <v-progress-linear
            color="green"
            indeterminate
            :style="imageUploadProgressBarStyle"
          ></v-progress-linear>
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col lg="8" cols="12">
          <div class="d-flex align-center">
            <div class="d-flex align-center" v-if="watchList.loaded == true && watchList.data.length > 0">
              <h1 class="mr-3">내가 본 작품 리스트</h1>
              <v-btn class="mr-3" color="primary">펼쳐보기</v-btn>
              <v-icon icon="mdi-cog" size="large" @click="toggleShowOptions('collection-primary')"/>
            </div>
            <div v-else>
              <h1>내가 본 작품 리스트</h1>
            </div>
          </div>
          <div v-if="watchList.loaded == true && watchList.data.length > 0" class="position-relative">
            <swiper-container 
              class="slider"
              space-between="30"
              slides-per-view="5"
            >
              <swiper-slide 
                class="slide"
                v-for="(item, index) in watchList.data"
              >
                <v-sheet class="ma-3 py-3 w-100 h-100" :elevation="10">
                  <RouterLink
                    class="w-100 h-100 d-flex flex-column justify-center align-center"
                    :to="{path: '/detail', query: {id : item.mediaId}}"
                    v-on:click.prevent="handleClick(item.mediaId, backDropPath(item.backDropPath))">
                    <img
                      :src="posterPath(item.posterPath)"
                      class="poster mb-1"
                    ></img>
                    <div>{{ shorten(item.mediaTitle) }}</div>
                  </RouterLink>
                </v-sheet>
              </swiper-slide>
            </swiper-container>
            <div class="backdrop" id="collection-primary">
              <div class="position-relative d-flex justify-center align-center w-100 h-100">
                <div>
                  <v-checkbox style="color: white;" color="primary" density="compact" label="리스트 공개하기"
                    v-model="optionStates['collection-primary'].options.publicList"></v-checkbox>
                  <v-checkbox color="primary" density="compact" label="댓글 허용"
                    v-model="optionStates['collection-primary'].options.allowReply"></v-checkbox>
                </div>
                <div class="position-absolute ma-3 top-0 right-0">
                  <v-icon icon="mdi-window-close" size="large" @click="closeShowOptions('collection-primary')"/>
                </div>
              </div>
            </div>
          </div>
        </v-col>
      </v-row>
    </v-container>
    <Background3Dmodel class="background-model"/>
</template>

<style>
  /* reference : https://codepen.io/tony19/pen/PoWMroG */
  .v-input__control .v-label {
    opacity: 100%;
  }

  #image-upload-card-container {
    height: 0px;
    /* display: none; */
    transition: height 0.7s ease-in-out, opacity 1s ease-in-out;
    opacity: 0;
  }

  #profile-backdrop {
    width: 100%;
    height: 300px;
    margin-bottom: 100px;
  }

  #profile-contents {
    padding-left: 20px;
    bottom: -75px;
  }

  #profile-image-frame {
    width: 150px;
    height: 150px;
  }

  #profile-image {
    overflow: hidden; /* 원형 틀 밖의 이미지 숨김 */
    border-radius: 50%; /* 원형으로 자르기 */
    display: flex;
    justify-content: center;
    align-items: center;
  }

  #profile-image-change-btn {
    width: 35px;
    height: 35px;
    bottom: 0%;
    right: 0%;
    background-color: white;
    border: 1px solid;
    border-color: gray;
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  #profile-image-editing {
    width: 100%;
    aspect-ratio: 1 / 1;
  }

  @keyframes flash {
    50% {
      background-color: white;
    }
  }

  .background-model {
    position: fixed;
    bottom: 0px;
    right: 0px;
    opacity: 0.7;
    z-index: 1;
  }

  .backdrop {
    position: absolute;
    top: 0%;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.7);
    z-index: 1;
    display: none;
    color: white;
  }

  .slider {
    /* border: 1px solid; */
    /* background-color: rgb(185, 185, 255); */
  }

  .slide {
    box-sizing: border-box;
    /* width: 350px; */
    /* border: 1px solid; */
    /* border-radius: 5%;
    background-color: rgb(185, 185, 255);
    width: 200px; */
  }
</style>

<script setup>
  import { register } from 'swiper/element/bundle'
  register();

  import AppHeader from '@/components/AppHeader.vue';
  import { reactive, ref, watch, computed, nextTick } from 'vue'
  import axios from 'axios'

  let userId = ref('')
  let watchList = ref({data: [], loaded: false})
  const posterBaseUrl = "http://image.tmdb.org/t/p/w500/"
  const noImageUrl = "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg?20200913095930"
  let optionStates = reactive({})
  let imageUploadCardStyle = ref('bg-yellow-lighten-5') // ex) 'bg-grey-lighten-2 pa-3'
  let imageUploadMessage = ref('')
  let imageUploadMessageCardClass = ref('') // ex) 'bg-grey-lighten-2 pa-3'
  let imageUploadMessageCardStyle = ref('display: none;') // ex) 'display: none;'
  let imageUploadProgressBarStyle = ref('display: none;')
  let successUpload = ref('before') // 'before' or 'complete'

  let showimageUploadProgressBar = ((bool) => {
    if (bool == true) {
      imageUploadProgressBarStyle.value = 'display: block;'
    } else {
      imageUploadProgressBarStyle.value = 'display: none;'
    }
  })

  watch(() => {return watchList.value.loaded}, (bool) => {
    if (bool == true) {
      // NOTE : 반응형 객체에 값을 동적으로 추가하기 위해 ref()가 아니라 reactive()를 사용
      // NOTE : reactive에서는 optionsStates.value로 접근하지 않음
      optionStates["collection-primary"] = {
        "visible" : false,
        "options" : {
          "publicList" : true,
          "allowReply" : false,
        }
      }
      console.log("watchList loaded...")
      console.log(optionStates)
      console.log("watchList loaded end")
      // TODO : 서버에 보내서 옵션 상태 갱신하기
      // TODO : 서버에서 갱신이 안되면 토스트로 에러 메시지 띄우기
      // allowReply 해제할 때 답글이 있으면 '지금까지 달린 답글은 비공개처리됩니다'라고 토스트 띄우기
    }
  })

  async function getUser() {
    let response = await axios.get(
      'http://localhost:8080/api/authenticated',
      {
        withCredentials: true,

        headers: {
          "X-Requested-With": "XMLHttpRequest"
        }
      }
    ) // axios.get end
    if (!response.data.principal) { // 로그인 안되어 있음
      return 
    }
    userId.value = response.data.principal.username
  }
  getUser()

  async function getWatchList() {
    let response = await axios.get(
      'http://localhost:8080/api/collection/watchList',
      {
        withCredentials: true,

        headers: {
          "X-Requested-With": "XMLHttpRequest"
        }
      }
    ) // axios.get end
    console.log("response : ")
    console.log( response)
    watchList.value.data = response.data
    watchList.value.loaded = true
  }
  getWatchList()

  const backDropPath = (string) => {
    if (string) {
      return posterBaseUrl + string 
    } else {
      noImageUrl
    }
  }

  const posterPath = (string) => {
    if (string) {
      return posterBaseUrl + string 
    } else {
      noImageUrl
    }
  }

  const shorten = (string) => {
    if (string.length > 12) {
      string = string.substr(0, 12)
      string += "..."
    }
    return string
  }

  // TODO : id도 굳이 pinia에 저장할 필요 있나?
  // TODO : "movie"인지, "tv"인지를 저장해야 함(그것에 따라 요청해야 하는 외부 api가 다름)
  import { useMediaDetailStore } from '@/components/stores/MediaDetail'
  import Background3Dmodel from './Background3Dmodel.vue';
  const store = useMediaDetailStore()
  const handleClick = (mediaId, backDropPath) => {
    store.setMediaDetail({
      id: mediaId,
      backDropPath: backDropPath
    })
  }

  const openUpdateProfileSection = () => {
    // 초기화
    successUpload.value = 'before'
    imageUploadCardStyle.value = 'bg-yellow-lighten-5'
    imageUploadMessageCardStyle.value = "display: none;"

    let cardContainerElement = document.getElementById('image-upload-card-container')
    nextTick(() => {
      if (cardContainerElement.style.height == '0px' || cardContainerElement.style.height == '') {
        cardContainerElement.style.opacity = '1';
        const targetHeight = cardContainerElement.scrollHeight + 'px';
        cardContainerElement.style.height = targetHeight;
      }
    })
  }

  const closeUpdateProfileSection = () => {
    let cardContainerElement = document.getElementById('image-upload-card-container')
    nextTick(() => {
      cardContainerElement.style.opacity = '0';
      const targetHeight = '0px'
      cardContainerElement.style.height = targetHeight;
    })
  }

  const toggleShowOptions = (elementId) => {
    console.log("toggleShowOptions...")
    let element = document.getElementById(elementId)
    let computedStyle = window.getComputedStyle(element).display;
    if (computedStyle === "none") {
      element.style.display = "flex";
    } else {
      element.style.display = "none";
    }
  }

  const closeShowOptions = (elementId) => {
    let element = document.getElementById(elementId)
    element.style.display = "none"
  }

  // '파일 찾아보기' 버튼을 눌렀을 때
  const onClickImageUploadBtn = () => {
    let element = document.getElementById("image-upload-input")
    element.click()
  }

  // '업로드' 버튼을 눌렀을 때
  const onClickSubmitImage = async () => {
    let fileInputElement = document.getElementById("image-upload-input")
    let file = fileInputElement.files[0]
    console.log(`파일 : ${fileInputElement.files[0]}`)
    if (!file) {
      // 0.5초 후 원상복귀
      setTimeout(() => {
        imageUploadCardStyle.value = 'bg-yellow-lighten-5'
      }, 500)
      imageUploadCardStyle.value = "bg-deep-orange-lighten-4"
      imageUploadMessageCardStyle.value = "display: block;"
      imageUploadMessageCardClass.value = "bg-deep-orange-lighten-1 pa-1"
      return imageUploadMessage.value = '😯파일을 업로드하지 않은 것 같아요'
    }
    console.log(`파일 크기 : ${fileInputElement.files[0].size}`)
    // 파일 크기가 1MB 초과할 시
    if (file.size > 1024 * 1024) {
      let originalStyle = imageUploadCardStyle.value
      // 0.5초 후 원상복귀
      setTimeout(() => {
        imageUploadCardStyle.value = originalStyle
      }, 500)
      imageUploadCardStyle.value = "bg-deep-orange-lighten-4"
      imageUploadMessageCardStyle.value = "display: block;"
      imageUploadMessageCardClass.value = "bg-deep-orange-lighten-1 pa-1"
      return imageUploadMessage.value = '😢파일 사이즈가 너무 커요'
    }
    // TODO : 이미지 파일이 아닐 시 (MIME 타입 검증증)

    showimageUploadProgressBar(true)

    let formData = new FormData();
    formData.append("profileImage", file);
    
    try {
      let response = await axios.post(
        'http://localhost:8080/api/members/profile/image',
        formData, // 본문
        {
          withCredentials: true,
          headers: {
            "X-Requested-With": "XMLHttpRequest",
            "Content-Type": "multipart/form-data",
          },

        }
      )
    } catch (error) {
      if (error.response.status == 400) {
        setTimeout(() => {
            imageUploadCardStyle.value = 'bg-yellow-lighten-5'
          }, 500)
          imageUploadCardStyle.value = "bg-deep-orange-lighten-4"
          imageUploadMessageCardStyle.value = "display: block;"
          imageUploadMessageCardClass.value = "bg-deep-orange-lighten-1 pa-1"
          showimageUploadProgressBar(false)
          return imageUploadMessage.value = '🤔업로드한 파일이 올바르지 않아요. 다른 파일로 시도해보세요.'
        }
      else if (error.response.status == 500) {
        // 0.5초 후 원상복귀
        setTimeout(() => {
          imageUploadCardStyle.value = 'bg-yellow-lighten-5'
          }, 500)
        imageUploadCardStyle.value = "bg-red-lighten-2"
        imageUploadMessageCardStyle.value = "display: block;"
        imageUploadMessageCardClass.value = "bg-red pa-1"
        showimageUploadProgressBar(false)
        return imageUploadMessage.value = '😵서버에 문제가 있는 것 같아요. 잠시 후 다시 시도해주세요.'
      }
    }

    
    // 프로필 이미지 재요청
    document.querySelector("#profile-image img").src = `http://localhost:8080/api/members/profile/image?timestamp=${new Date().getTime()}`
    imageUploadCardStyle.value = "bg-green-lighten-4"
    imageUploadMessageCardStyle.value = "display: block;"
    imageUploadMessageCardClass.value = "bg-green-lighten-2 pa-1"
    showimageUploadProgressBar(false)
    imageUploadMessage.value = '😀프로필이 성공적으로 변경되었어요!'
    successUpload.value = 'complete'
  }

  // '취소' 버튼을 눌렀을 때
  const onClickCancelImage = () => {

  }

  const onChangeImageUploadInput = (event) => {
    console.log("onChangeImageUploadInput...")
    console.log(event)
    console.log(event.target)
    let previewImgElement = document.getElementById("profile-image-editing")
    let file = event.target.files[0]
    if (file) {
      let reader = new FileReader()
      reader.onload = (e) => {
        previewImgElement.src = e.target.result
      }
      reader.readAsDataURL(file)
    }
  }
</script>