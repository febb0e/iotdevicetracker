<template>
	<div class="min-h-full flex flex-col justify-center py-12 sm:px-6 lg:px-8">
		<div class="sm:mx-auto sm:w-full sm:max-w-md">
			<div class="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
				<div class="flex justify-between items-center">
					<h3 class="flex text-lg leading-6 font-medium text-gray-900">
						<LoginIcon class="h-5 w-5 mr-2" /> Login
					</h3>
					<nuxt-link
						to="/auth/register"
						class="text-sm font-medium text-indigo-600 hover:text-indigo-500"
					>
						Register
					</nuxt-link>
				</div>

				<div
					v-if="requiresTOTP && !invalidCredentials"
					class="mt-6 rounded-md bg-indigo-50 p-4"
				>
					<div class="flex">
						<div class="flex-shrink-0">
							<InformationCircleIcon
								class="h-5 w-5 text-indigo-400"
								aria-hidden="true"
							></InformationCircleIcon>
						</div>
						<div class="ml-3">
							<h3 class="text-sm font-medium text-indigo-800">
								You have enabled 2 factor authentication. Please enter your
								TOTP.
							</h3>
						</div>
					</div>
				</div>

				<div v-if="invalidCredentials" class="mt-6 rounded-md bg-red-50 p-4">
					<div class="flex">
						<div class="flex-shrink-0">
							<XCircleIcon
								class="h-5 w-5 text-red-400"
								aria-hidden="true"
							></XCircleIcon>
						</div>
						<div class="ml-3">
							<h3 class="text-sm font-medium text-red-800">
								<span v-if="requiresTOTP">Invalid TOTP</span>
								<span v-else>Invalid credentials</span>
							</h3>
						</div>
					</div>
				</div>

				<form class="mt-6 space-y-6" @submit.prevent="login">
					<div>
						<label for="login" class="block text-sm font-medium text-gray-700">
							Login
						</label>
						<div class="mt-1">
							<input
								v-model="loginData.login"
								id="login"
								name="login"
								type="text"
								autocomplete="username"
								placeholder="Jane Doe"
								required="true"
								class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
							/>
						</div>
					</div>

					<div>
						<label
							for="password"
							class="block text-sm font-medium text-gray-700"
						>
							Password
						</label>
						<div class="mt-1">
							<input
								v-model="loginData.password"
								id="password"
								name="password"
								type="password"
								autocomplete="current-password"
								placeholder="********"
								required="true"
								class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
							/>
						</div>
					</div>

					<div v-show="requiresTOTP">
						<label for="totp" class="block text-sm font-medium text-gray-700">
							TOTP
						</label>
						<div class="mt-1">
							<input
								v-model="loginData.totp"
								ref="totpInput"
								id="totp"
								name="totp"
								type="text"
								autocomplete="one-time-code"
								placeholder="123456"
								:required="requiresTOTP"
								class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
							/>
						</div>
					</div>

					<div class="flex items-center justify-between">
						<div class="flex items-center">
							<input
								v-model="loginData.rememberMe"
								id="remember-me"
								name="remember-me"
								type="checkbox"
								class="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
							/>
							<label for="remember-me" class="ml-2 block text-sm text-gray-900">
								Remember me
							</label>
						</div>

						<div class="text-sm">
							<nuxt-link
								to="/auth/password-reset"
								class="font-medium text-indigo-600 hover:text-indigo-500"
							>
								Forgot your password?
							</nuxt-link>
						</div>
					</div>

					<div>
						<button
							type="submit"
							class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
						>
							<span v-if="isLoggingIn">Checking credentials...</span>
							<span v-else>Sign in</span>
						</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
	import { LoginIcon } from '@heroicons/vue/solid/index.js'
	import {
		XCircleIcon,
		InformationCircleIcon,
	} from '@heroicons/vue/solid/index.js'
	import { NotificationType, useNotificationStore } from '~/stores/notification'
	import { useStore } from '~/stores/main'
	import { $api, isValidationError } from '~/composables/useApi'

	const store = useStore()
	const notification = useNotificationStore()
	const router = useRouter()
	const loginData = reactive({
		login: '',
		password: '',
		totp: '',
		rememberMe: false,
	})
	const totpInput = ref<HTMLInputElement | null>(null)
	const isLoggingIn = ref(false)
	const invalidCredentials = ref(false)
	const requiresTOTP = ref(false)

	async function login() {
		if (isLoggingIn.value) return
		isLoggingIn.value = true

		try {
			const user = await $api<User>('/auth/login', {
				method: 'POST',
				params: { rememberMe: loginData.rememberMe },
				body: {
					login: loginData.login,
					password: loginData.password,
					totp: loginData.totp,
				},
			})
			invalidCredentials.value = false
			store.authenticatedUser = user
			notification.show(
				NotificationType.SUCCESS,
				`Welcome back ${user.username}!`,
				'Successfully logged in',
				10
			)
			if (user.verified) {
				router.push('/')
			} else {
				router.push('/auth/verify')
			}
		} catch (err) {
			if (isValidationError(err)) {
				if (err.response?._data === 'Invalid TOTP') {
					if (requiresTOTP.value) {
						invalidCredentials.value = true
					} else {
						invalidCredentials.value = false
						requiresTOTP.value = true
						nextTick().then(() => {
							if (totpInput.value) {
								totpInput.value.focus()
							}
						})
					}
				} else {
					invalidCredentials.value = true
					requiresTOTP.value = false
				}
			} else {
				notification.internalServerError()
				console.error(err)
			}
		} finally {
			isLoggingIn.value = false
		}
	}
</script>
