<template>
	<div class="min-h-full flex flex-col justify-center py-12 sm:px-6 lg:px-8">
		<div class="sm:mx-auto sm:w-full sm:max-w-md">
			<div class="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
				<div class="flex justify-between items-center">
					<h3 class="flex text-lg leading-6 font-medium text-gray-900">
						<LoginIcon class="h-5 w-5 mr-2" /> Register
					</h3>
					<nuxt-link
						to="/auth/login"
						class="text-sm font-medium text-indigo-600 hover:text-indigo-500"
					>
						Login
					</nuxt-link>
				</div>

				<form class="mt-6 space-y-6" @submit.prevent="register">
					<div>
						<label
							for="username"
							class="block text-sm font-medium text-gray-700"
						>
							Username
						</label>
						<div class="mt-1 relative">
							<input
								v-model="registrationData.username"
								id="username"
								name="username"
								autocomplete="username"
								placeholder="Jane Doe"
								required="true"
								:class="[
									'appearance-none block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400 focus:outline-none sm:text-sm',
									hasValidationErrors('username')
										? 'border-red-300 text-red-900 focus:ring-red-500 focus:border-red-500'
										: 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500',
								]"
								:aria-invalid="hasValidationErrors('username')"
								aria-describedby="username-error"
								@input="clearValidationError('username')"
							/>
							<div
								v-show="hasValidationErrors('username')"
								class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none"
							>
								<ExclamationCircleIcon
									class="h-5 w-5 text-red-500"
									aria-hidden="true"
								/>
							</div>
						</div>
						<p
							v-if="hasValidationErrors('username')"
							id="username-error"
							class="mt-2 text-sm text-red-600"
						>
							{{ validationErrors.username.defaultMessage }}
						</p>
					</div>

					<div>
						<label for="email" class="block text-sm font-medium text-gray-700">
							Email address
						</label>
						<div class="mt-1 relative">
							<input
								v-model="registrationData.email"
								id="email"
								name="email"
								type="email"
								autocomplete="email"
								placeholder="jane.doe@email.net"
								required="true"
								:class="[
									'appearance-none block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400 focus:outline-none sm:text-sm',
									hasValidationErrors('email')
										? 'border-red-300 text-red-900 focus:ring-red-500 focus:border-red-500'
										: 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500',
								]"
								:aria-invalid="hasValidationErrors('email')"
								aria-describedby="email-error"
								@input="clearValidationError('email')"
							/>
							<div
								v-show="hasValidationErrors('email')"
								class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none"
							>
								<ExclamationCircleIcon
									class="h-5 w-5 text-red-500"
									aria-hidden="true"
								/>
							</div>
						</div>
						<p
							v-if="hasValidationErrors('email')"
							id="email-error"
							class="mt-2 text-sm text-red-600"
						>
							{{ validationErrors.email.defaultMessage }}
						</p>
					</div>

					<div>
						<label
							for="password"
							class="block text-sm font-medium text-gray-700"
						>
							Password
						</label>
						<div class="mt-1 relative">
							<input
								v-model="registrationData.password"
								id="password"
								name="password"
								type="password"
								autocomplete="new-password"
								placeholder="********"
								required="true"
								:class="[
									'appearance-none block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400 focus:outline-none sm:text-sm',
									hasValidationErrors('password')
										? 'border-red-300 text-red-900 focus:ring-red-500 focus:border-red-500'
										: 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500',
								]"
								:aria-invalid="hasValidationErrors('password')"
								aria-describedby="password-error"
								@input="clearValidationError('password')"
							/>
							<div
								v-show="hasValidationErrors('password')"
								class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none"
							>
								<ExclamationCircleIcon
									class="h-5 w-5 text-red-500"
									aria-hidden="true"
								/>
							</div>
						</div>
						<p
							v-if="hasValidationErrors('password')"
							if="password-error"
							class="mt-2 text-sm text-red-600"
						>
							{{ validationErrors.password.defaultMessage }}
						</p>
					</div>

					<div>
						<label
							for="passwordConfirmation"
							class="block text-sm font-medium text-gray-700"
						>
							Confirm password
						</label>
						<div class="mt-1 relative">
							<input
								v-model="registrationData.passwordConfirmation"
								id="passwordConfirmation"
								name="passwordConfirmation"
								type="password"
								autocomplete="new-password"
								placeholder="********"
								required="true"
								:class="[
									'appearance-none block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400 focus:outline-none sm:text-sm',
									hasValidationErrors('passwordConfirmation')
										? 'border-red-300 text-red-900 focus:ring-red-500 focus:border-red-500'
										: 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500',
								]"
								:aria-invalid="hasValidationErrors('passwordConfirmation')"
								aria-describedby="passwordConfirmation-error"
								@input="clearValidationError('passwordConfirmation')"
							/>
							<div
								v-show="hasValidationErrors('passwordConfirmation')"
								class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none"
							>
								<ExclamationCircleIcon
									class="h-5 w-5 text-red-500"
									aria-hidden="true"
								/>
							</div>
						</div>
						<p
							v-if="hasValidationErrors('passwordConfirmation')"
							id="passwordConfirmation-error"
							class="mt-2 text-sm text-red-600"
						>
							{{ validationErrors.passwordConfirmation.defaultMessage }}
						</p>
					</div>

					<div>
						<button
							type="submit"
							class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
						>
							<span v-if="isRegistering">Registering ...</span>
							<span v-else>Register</span>
						</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
	import {
		LoginIcon,
		ExclamationCircleIcon,
	} from '@heroicons/vue/solid/index.js'
	import { $api } from '~/composables/useApi'
	import { FetchError } from 'ohmyfetch'
	import { NotificationType, useNotificationStore } from '~/stores/notification'
	import { useValidationErrors } from '~/composables/useValidationErrors'
	import { useStore } from '~/stores/main'

	const router = useRouter()
	const store = useStore()
	const notification = useNotificationStore()
	const {
		validationErrors,
		hasValidationErrors,
		clearValidationError,
		fillValidationErrors,
	} = useValidationErrors()

	const registrationData = reactive({
		username: '',
		email: '',
		password: '',
		passwordConfirmation: '',
	})

	const isRegistering = ref(false)

	async function register() {
		if (hasValidationErrors()) {
			notification.show(
				NotificationType.ERROR,
				'Validation failed',
				'One or more fields are invalid. Please fix validation errors, before trying again.',
				10
			)
			return
		}

		if (isRegistering.value) return
		isRegistering.value = true
		try {
			const user = await $api<User>('/auth/register', {
				method: 'POST',
				body: registrationData,
			})
			store.authenticatedUser = user
			notification.show(
				NotificationType.SUCCESS,
				`Welcome onboard, ${user.username}!`,
				'Successfully registered',
				10
			)
			router.push('/auth/verify')
		} catch (err) {
			if (err instanceof FetchError && err.response.status === 422) {
				fillValidationErrors(err)
			} else {
				notification.internalServerError()
				console.error(err)
			}
		} finally {
			isRegistering.value = false
		}
	}
</script>
