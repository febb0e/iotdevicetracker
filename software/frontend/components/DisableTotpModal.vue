<!-- This example requires Tailwind CSS v2.0+ -->
<template>
	<TransitionRoot as="template" :show="open">
		<Dialog as="div" class="fixed z-10 inset-0 overflow-y-auto" @close="close">
			<div
				class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0"
			>
				<TransitionChild
					as="template"
					enter="ease-out duration-300"
					enter-from="opacity-0"
					enter-to="opacity-100"
					leave="ease-in duration-200"
					leave-from="opacity-100"
					leave-to="opacity-0"
				>
					<DialogOverlay
						class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"
					/>
				</TransitionChild>

				<!-- This element is to trick the browser into centering the modal contents. -->
				<span
					class="hidden sm:inline-block sm:align-middle sm:h-screen"
					aria-hidden="true"
					>&#8203;</span
				>
				<TransitionChild
					as="template"
					enter="ease-out duration-300"
					enter-from="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
					enter-to="opacity-100 translate-y-0 sm:scale-100"
					leave="ease-in duration-200"
					leave-from="opacity-100 translate-y-0 sm:scale-100"
					leave-to="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
				>
					<div
						class="inline-block align-bottom bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full sm:p-6"
					>
						<form @submit.prevent="disable">
							<div class="sm:flex sm:items-start">
								<div
									class="mx-auto flex-shrink-0 flex items-center justify-center h-12 w-12 rounded-full bg-red-100 sm:mx-0 sm:h-10 sm:w-10"
								>
									<ExclamationIcon
										class="h-6 w-6 text-red-600"
										aria-hidden="true"
									/>
								</div>
								<div
									class="mt-3 w-full text-center sm:mt-0 sm:ml-4 sm:text-left"
								>
									<DialogTitle
										as="h3"
										class="text-lg leading-6 font-medium text-gray-900"
									>
										Disable 2-Factor-Authentication
									</DialogTitle>
									<div class="mt-2">
										<p class="text-sm text-gray-500">
											Are you sure you want to disable 2-Factor-Authentication?
											This will decrease your account security.
										</p>

										<div class="mt-4 mb-2">
											<label
												for="password"
												class="block text-sm font-medium text-gray-700"
											>
												Password
											</label>
											<div class="mt-1 relative">
												<input
													v-model="password"
													id="password"
													name="password"
													type="password"
													autocomplete="current-password"
													placeholder="********"
													required="true"
													:class="[
														'appearance-none block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400 focus:outline-none sm:text-sm',
														invalidPassword
															? 'border-red-300 text-red-900 focus:ring-red-500 focus:border-red-500'
															: 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500',
													]"
													:aria-invalid="invalidPassword"
													aria-describedby="password-error"
													@input="invalidPassword = false"
												/>
												<div
													v-show="invalidPassword"
													class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none"
												>
													<ExclamationCircleIcon
														class="h-5 w-5 text-red-500"
														aria-hidden="true"
													/>
												</div>
											</div>
											<p
												v-if="invalidPassword"
												if="password-error"
												class="mt-2 text-sm text-red-600"
											>
												Invalid password
											</p>
										</div>
									</div>
								</div>
							</div>
							<div class="mt-5 sm:mt-4 sm:flex sm:flex-row-reverse">
								<button
									type="submit"
									class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-red-600 text-base font-medium text-white hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 sm:ml-3 sm:w-auto sm:text-sm"
								>
									<span v-if="isDisabling">Disabling ...</span>
									<span v-else>Disable</span>
								</button>
								<button
									type="button"
									class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:w-auto sm:text-sm"
									@click="close"
								>
									Cancel
								</button>
							</div>
						</form>
					</div>
				</TransitionChild>
			</div>
		</Dialog>
	</TransitionRoot>
</template>

<script lang="ts" setup>
	import { ref } from 'vue'
	import {
		Dialog,
		DialogOverlay,
		DialogTitle,
		TransitionChild,
		TransitionRoot,
	} from '@headlessui/vue'
	import { ExclamationIcon } from '@heroicons/vue/outline/index.js'
	import { ExclamationCircleIcon } from '@heroicons/vue/solid/index.js'
	import { useNotificationStore } from '~/stores/notification'
	import { $api } from '~/composables/useApi'
	import { isUnauthorizedError, isValidationError } from '../composables/useApi'

	interface Props {
		open: boolean
	}

	interface Emits {
		(e: 'close'): void
		(e: 'disabled'): void
	}

	const props = withDefaults(defineProps<Props>(), { open: false })
	const emit = defineEmits<Emits>()
	const notification = useNotificationStore()

	const password = ref('')
	const invalidPassword = ref(false)
	const isDisabling = ref(false)

	async function disable() {
		if (isDisabling.value) return
		isDisabling.value = true

		try {
			await $api(`/totp/disable`, {
				method: 'POST',
				body: {
					password: password.value,
				},
			})
			emit('disabled')
			close()
		} catch (err) {
			if (isValidationError(err)) {
				invalidPassword.value = true
			} else if (!isUnauthorizedError(err)) {
				notification.internalServerError()
				console.error(err)
			}
		} finally {
			isDisabling.value = false
		}
	}

	function close() {
		emit('close')
		password.value = ''
		invalidPassword.value = false
	}
</script>
