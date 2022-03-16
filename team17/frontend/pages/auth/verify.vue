<template>
	<div class="min-h-full flex flex-col justify-center py-12 sm:px-6 lg:px-8">
		<div class="sm:mx-auto sm:w-full sm:max-w-md">
			<div class="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
				<div class="flex justify-between items-center">
					<h3 class="flex text-lg leading-6 font-medium text-gray-900">
						E-Mail Verification
					</h3>
				</div>

				<p class="text-sm mt-2">
					An E-Mail with a verification token has been sent to
					{{ store.authenticatedUser.email }}. Please enter this token to verify
					your account.
				</p>

				<div v-show="tokenInvalid" class="mt-6 rounded-md bg-red-50 p-4">
					<div class="flex">
						<div class="flex-shrink-0">
							<XCircleIcon
								class="h-5 w-5 text-red-400"
								aria-hidden="true"
							></XCircleIcon>
						</div>
						<div class="ml-3">
							<h3 class="text-sm font-medium text-red-800">Invalid token</h3>
						</div>
					</div>
				</div>

				<form class="mt-6 space-y-6" @submit.prevent="verify">
					<div>
						<label for="login" class="block text-sm font-medium text-gray-700">
							Verification token
						</label>
						<div class="mt-1">
							<input
								v-model="token"
								id="token"
								name="token"
								type="text"
								placeholder="XXXXXX"
								required="true"
								class="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
							/>
						</div>
					</div>

					<div>
						<button
							type="submit"
							class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
						>
							<span v-if="isVerifying">Checking token...</span>
							<span v-else>Verify</span>
						</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
	import { useStore } from '~/stores/main'
	import { $api, isValidationError } from '~/composables/useApi'
	import { XCircleIcon } from '@heroicons/vue/solid/index.js'
	import { NotificationType, useNotificationStore } from '~/stores/notification'

	const store = useStore()
	const notification = useNotificationStore()
	const router = useRouter()
	const token = ref('')
	const tokenInvalid = ref(false)
	const isVerifying = ref(false)

	async function verify() {
		if (isVerifying.value) return
		isVerifying.value = true

		try {
			const user = await $api<User>('/auth/verify', {
				method: 'POST',
				params: { token: token.value },
			})
			store.authenticatedUser = user
			notification.show(
				NotificationType.SUCCESS,
				'Success!',
				'Your account has been verified successfully',
				10
			)
			router.push('/')
		} catch (err) {
			if (isValidationError(err)) {
				tokenInvalid.value = true
			} else if (!isUnauthorizedError(err)) {
				notification.internalServerError()
				console.error(err)
			}
		} finally {
			isVerifying.value = false
		}
	}
</script>
