dnd = angular.module 'dnd', ['ngDraggable']

.controller 'DndController', class
  constructor: ->
    console.log('Init')
    @list1 = ['Bob', 'John', 'Jane']
    @list2 = []

  onDragList1: ->
    console.log('Drag from list1')

  onDragList2: ->
    console.log('Drag from list2')

  onEnterList2: ->
    console.log('Enter list2')

  onDropList1: (data) ->
    return unless data?
    console.log('Drop on list1')
    @removeFromLists(data)
    @list1.push(data)

  onDropList2: (data) ->
    return unless data?
    console.log('Drop on list2')
    @removeFromLists(data)
    @list2.push(data)

  removeFromLists: (data) ->
    @list2 = (item for item in @list2 when item isnt data)
    @list1 = (item for item in @list1 when item isnt data)
