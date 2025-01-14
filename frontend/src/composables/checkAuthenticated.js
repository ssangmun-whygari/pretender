import axios from 'axios'

export async function useCheckAuthenticated() {
  let response = await axios.get(
    'http://localhost:8080/api/authenticated',
    {
      withCredentials: true,

      headers: {
        "X-Requested-With": "XMLHttpRequest"
      }
    }
  ) // axios.get end
  console.log(`authenticated? : ${response.data && response.data.authenticated == true}`)
  return response.data && response.data.authenticated == true
}