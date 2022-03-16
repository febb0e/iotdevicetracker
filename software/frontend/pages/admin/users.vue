<template>
	<div class="mt-6">
		<div
			class="flex justify-between items-center mb-4 pb-5 border-b border-gray-200"
		>
			<h3 class="text-lg leading-6 font-medium text-gray-900">Users</h3>
			<button
				type="button"
				class="px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:ml-3"
				@click="refresh()"
			>
				<span v-if="pending">Reloading ...</span>
				<span v-else>Reload</span>
			</button>
		</div>

		<div class="mt-5">
			<label for="search" class="sr-only">Search username</label>
			<div class="mt-1 relative rounded-md shadow-sm">
				<div
					class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none"
					aria-hidden="true"
				>
					<SearchIcon class="mr-3 h-4 w-4 text-gray-400" aria-hidden="true" />
				</div>
				<input
					v-model="username"
					type="text"
					name="search"
					class="focus:ring-indigo-500 focus:border-indigo-500 block w-full pl-9 sm:text-sm border-gray-300 rounded-md"
					placeholder="Search username"
					@keyup.enter="refresh"
				/>
			</div>
		</div>

		<div class="flex flex-col mt-4">
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
										Username
									</th>
									<th
										scope="col"
										class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
									>
										Email
									</th>
									<th
										scope="col"
										class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
									>
										Verified
									</th>
									<th
										scope="col"
										class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
									>
										MFA Enabled
									</th>
									<th
										scope="col"
										class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
									>
										Role
									</th>
									<th scope="col"></th>
								</tr>
							</thead>
							<tbody>
								<UserRow
									v-for="(user, userIndex) in data.users"
									:key="user.id"
									:class="userIndex % 2 === 0 ? 'bg-white' : 'bg-gray-50'"
									:user="user"
									@edited="onEdited"
									@deleted="onDeleted"
								></UserRow>
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
	import { SearchIcon } from '@heroicons/vue/solid/index.js'
	import { NotificationType, useNotificationStore } from '~/stores/notification'
	import { $api } from '~/composables/useApi'

	interface Data {
		totalUsers: number
		totalPages: number
		users: User[]
	}

	const notification = useNotificationStore()

	const page = ref(1)
	const username = ref('')
	const hasPreviousPage = computed(() => page.value > 1)
	const hasNextPage = computed(() => page.value < data.value.totalPages)

	const { data, pending, refresh } = await useAsyncData<Data>(
		'users',
		async () => {
			try {
				return await $api<Data>('/users', {
					params: { page: page.value, username: username.value },
				})
			} catch (err) {
				if (!isUnauthorizedError(err)) {
					notification.internalServerError()
					console.error(err)
				}
				return { totalUsers: 0, totalPages: 1, users: [] }
			}
		}
	)

	watch(page, () => refresh())

	function onEdited(user: User) {
		refresh()
		notification.show(
			NotificationType.SUCCESS,
			'Successfully updated',
			`Successfully updated user ${user.username}`,
			10
		)
	}

	function onDeleted(user: User) {
		refresh()
		notification.show(
			NotificationType.SUCCESS,
			'Successfully deleted',
			`Successfully deleted user ${user.username}`,
			10
		)
	}
</script>
