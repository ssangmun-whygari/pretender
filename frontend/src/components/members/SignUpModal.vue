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
</style>

<script setup>
</script>