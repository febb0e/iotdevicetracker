<!-- This example requires Tailwind CSS v2.0+ -->
<template>
	<TransitionRoot as="template" :show="props.open">
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
						class="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full"
					>
						<form @submit.prevent="create">
							<div class="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
								<div class="sm:flex sm:items-start">
									<div
										class="mt-3 w-full text-center sm:mt-0 sm:ml-4 sm:text-left"
									>
										<DialogTitle
											as="h3"
											class="text-lg leading-6 font-medium text-gray-900"
										>
											Create Device
										</DialogTitle>
										<div v-if="!token" class="mt-2 space-y-6">
											<p class="text-sm text-gray-500">
												A Device represents one real IoT device, from which
												metrics are send to IoT Device Tracker.
											</p>

											<div>
												<label
													for="identifier"
													class="block text-sm font-medium text-gray-700"
												>
													Identifier
												</label>
												<div class="mt-1 relative">
													<input
														v-model="data.identifier"
														name="identifier"
														placeholder="AA:BB:CC:DD:EE:FF"
														required="true"
														:class="[
															'appearance-none block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400 focus:outline-none sm:text-sm',
															hasValidationErrors('identifier')
																? 'border-red-300 text-red-900 focus:ring-red-500 focus:border-red-500'
																: 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500',
														]"
														:aria-invalid="hasValidationErrors('identifier')"
														aria-describedby="identifier-error"
														@input="clearValidationError('identifier')"
													/>
													<div
														v-show="hasValidationErrors('identifier')"
														class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none"
													>
														<ExclamationCircleIcon
															class="h-5 w-5 text-red-500"
															aria-hidden="true"
														/>
													</div>
												</div>
												<p
													v-if="hasValidationErrors('identifier')"
													id="identifier-error"
													class="mt-2 text-sm text-red-600"
												>
													{{ validationErrors.identifier.defaultMessage }}
												</p>
											</div>

											<div>
												<label
													for="name"
													class="block text-sm font-medium text-gray-700"
												>
													Name
												</label>
												<div class="mt-1 relative">
													<input
														v-model="data.name"
														name="name"
														placeholder="My Device"
														required="true"
														:class="[
															'appearance-none block w-full px-3 py-2 border rounded-md shadow-sm placeholder-gray-400 focus:outline-none sm:text-sm',
															hasValidationErrors('name')
																? 'border-red-300 text-red-900 focus:ring-red-500 focus:border-red-500'
																: 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500',
														]"
														:aria-invalid="hasValidationErrors('name')"
														aria-describedby="name-error"
														@input="clearValidationError('name')"
													/>
													<div
														v-show="hasValidationErrors('name')"
														class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none"
													>
														<ExclamationCircleIcon
															class="h-5 w-5 text-red-500"
															aria-hidden="true"
														/>
													</div>
												</div>
												<p
													v-if="hasValidationErrors('name')"
													id="name-error"
													class="mt-2 text-sm text-red-600"
												>
													{{ validationErrors.name.defaultMessage }}
												</p>
											</div>
										</div>
										<div v-else class="mt-2">
											<p>
												The device has been created successfully. Below, you can
												see the crentials for the device. Please store them at a
												secure location.
											</p>

											<div class="mt-6 rounded-md bg-yellow-50 p-4">
												<div class="flex">
													<div class="flex-shrink-0">
														<ExclamationIcon
															class="h-5 w-5 text-yellow-400"
															aria-hidden="true"
														/>
													</div>
													<div class="ml-3">
														<h3 class="text-sm font-medium text-yellow-800">
															You will only see this token a single time!
														</h3>
													</div>
												</div>
											</div>

											<div class="mt-6">
												<p>Identifier: {{ data.identifier }}</p>
												<p>Token: {{ token }}</p>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div
								class="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse"
							>
								<template v-if="!token">
									<button
										type="submit"
										class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-indigo-600 text-base font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:ml-3 sm:w-auto sm:text-sm"
										@click="create"
									>
										<span v-if="isCreating">Creating ...</span>
										<span v-else>Create</span>
									</button>
									<button
										type="button"
										class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
										@click="close"
									>
										Cancel
									</button>
								</template>
								<template v-else>
									<button
										type="button"
										class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-indigo-600 text-base font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:ml-3 sm:w-auto sm:text-sm"
										@click="close"
									>
										Done
									</button>
								</template>
							</div>
						</form>
					</div>
				</TransitionChild>
			</div>
		</Dialog>
	</TransitionRoot>
</template>

<script lang="ts" setup>
	import {
		Dialog,
		DialogOverlay,
		DialogTitle,
		TransitionChild,
		TransitionRoot,
	} from '@headlessui/vue'
	import { useNotificationStore } from '~/stores/notification'
	import {
		$api,
		isUnauthorizedError,
		isValidationError,
	} from '~/composables/useApi'
	import { useValidationErrors } from '~/composables/useValidationErrors'
	import {
		ExclamationCircleIcon,
		ExclamationIcon,
	} from '@heroicons/vue/solid/index.js'

	interface Props {
		open: boolean
		deviceGroupId: number
	}

	interface Emits {
		(e: 'close'): void
		(e: 'created', device: DeviceWithGroup): void
	}

	const props = withDefaults(defineProps<Props>(), { open: false })
	const emit = defineEmits<Emits>()
	const notification = useNotificationStore()
	const {
		validationErrors,
		hasValidationErrors,
		clearValidationError,
		fillValidationErrors,
	} = useValidationErrors()

	const isCreating = ref(false)

	const data = reactive({
		identifier: '',
		name: '',
	})

	const token = ref(null as string | null)

	async function create() {
		if (hasValidationErrors() || isCreating.value) return
		isCreating.value = true

		try {
			const device = await $api<DeviceWithToken>('/devices', {
				method: 'POST',
				body: {
					...data,
					deviceGroupId: props.deviceGroupId,
				},
			})
			token.value = device.token
			const { token: _, ...deviceWithoutToken } = device
			emit('created', deviceWithoutToken)
		} catch (err) {
			if (isValidationError(err)) {
				fillValidationErrors(err)
			} else if (!isUnauthorizedError(err)) {
				notification.internalServerError()
				console.error(err)
			}
		} finally {
			isCreating.value = false
		}
	}

	function close() {
		data.identifier = ''
		data.name = ''
		token.value = null
		emit('close')
	}
</script>
