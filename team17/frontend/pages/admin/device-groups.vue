<template>
	<div class="mt-6">
		<div
			class="flex justify-between items-center mb-4 pb-5 border-b border-gray-200"
		>
			<h3 class="text-lg leading-6 font-medium text-gray-900">Device Groups</h3>
			<button
				type="button"
				class="px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:ml-3"
				@click="refresh()"
			>
				<span v-if="pending">Reloading ...</span>
				<span v-else>Reload</span>
			</button>
		</div>

		<div class="flex flex-col">
			<div class="-my-2 overflow-x-auto sm:-mx-6 lg:-mx-8">
				<div class="py-2 align-middle inline-block min-w-full sm:px-6 lg:px-8">
					<div
						class="shadow overflow-hidden border-b border-gray-200 sm:rounded-lg"
					>
						<table class="min-w-full divide-y divide-gray-200">
							<thead class="bg-gray-50">
								<tr>
									<th
										scope="col"
										class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
									>
										Name
									</th>
									<th
										scope="col"
										class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
									>
										#Devices
									</th>

									<th scope="col" class="relative px-6 py-3">
										<span class="sr-only">Show</span>
									</th>
								</tr>
							</thead>
							<tbody>
								<tr
									v-for="(group, groupIndex) in data.deviceGroups"
									:key="group.id"
									:class="groupIndex % 2 === 0 ? 'bg-white' : 'bg-gray-50'"
								>
									<td
										class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900"
									>
										{{ group.name }}
									</td>
									<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
										{{ group.amountOfDevices }}
									</td>
									<td
										class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium"
									>
										<nuxt-link
											:to="`/device-groups/${group.id}`"
											class="text-indigo-600 hover:text-indigo-900"
											>Show</nuxt-link
										>
									</td>
								</tr>
							</tbody>
						</table>
						<!-- Pagination -->
						<nav
							class="bg-white px-4 py-3 flex items-center justify-between border-t border-gray-200 sm:px-6"
							aria-label="Pagination"
						>
							<div>
								<p class="text-sm text-gray-700">
									Page
									<span class="font-medium">{{ page }}</span> of
									<span class="font-medium">{{ data.totalPages }}</span>
								</p>
							</div>
							<div class="flex-1 flex justify-between sm:justify-end">
								<button
									v-show="hasPreviousPage"
									class="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
									@click="page--"
								>
									Previous
								</button>
								<button
									v-show="hasNextPage"
									class="ml-3 relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
									@click="page++"
								>
									Next
								</button>
							</div>
						</nav>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
	import { useNotificationStore } from '~/stores/notification'
	import { $api } from '~/composables/useApi'

	interface Data {
		totalDeviceGroups: number
		totalPages: number
		deviceGroups: DeviceGroup[]
	}

	const notification = useNotificationStore()

	const page = ref(1)
	const hasPreviousPage = computed(() => page.value > 1)
	const hasNextPage = computed(() => page.value < data.value.totalPages)

	const { data, pending, refresh } = await useAsyncData<Data>(
		'users',
		async () => {
			try {
				return await $api<Data>('/device-groups', {
					params: { page: page.value, all: true },
				})
			} catch (err) {
				if (!isUnauthorizedError(err)) {
					notification.internalServerError()
					console.error(err)
				}
				return { totalDeviceGroups: 0, totalPages: 1, deviceGroups: [] }
			}
		}
	)

	watch(page, () => refresh())
</script>
