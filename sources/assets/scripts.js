function IndexController() {
    const menuState = document.querySelector('.js-menu-state')
    const firstLevelMenuItemStateList = document.querySelectorAll(
        '.js-menu-item-state-first-level'
    )
    const secondLevelMenuItemStateList = document.querySelectorAll(
        '.js-menu-item-state-second-level'
    )

    this.init = function () {
        bind()
        openAllFirstLevelMenuIfNecessary()
    }

    const bind = function () {
        menuState.addEventListener('change', function (event) {
            toggleMenu(event.target)
        })

        firstLevelMenuItemStateList.forEach(function (item) {
            item.addEventListener(
                'change',
                function (event) {
                    const stateElement = event.target
                    hideAllExcept(stateElement)
                    toggleSubmenu(stateElement, stateElement.checked)
                },
                { passive: true }
            )
        })

        secondLevelMenuItemStateList.forEach(function (item) {
            item.addEventListener(
                'change',
                function (event) {
                    const stateElement = event.target
                    toggleSubmenu(stateElement, stateElement.checked)
                },
                { passive: true }
            )
        })
    }

    const openAllFirstLevelMenuIfNecessary = function () {
        firstLevelMenuItemStateList.forEach(function (item) {
            if (!item.checked) return;
            toggleSubmenu(item, true)
        })
    }

    const toggleMenu = function (stateElement) {
        stateElement.disabled = true
        const isOpened = stateElement.checked

        const wrapper = stateElement.parentElement
        wrapper.style.overflowY = isOpened ? 'auto' : 'hidden'
        const menu = wrapper.querySelector(
            '.slide-left-on-toggle'
        )

        const elementList = Array.from(menu.children)
        if (!isOpened) {
            elementList.reverse()
            hideAllExcept(null)
        }

        const xPosition = isOpened ? '0%' : '125%'
        elementList.forEach(function (element, index) {
            setTimeout(function () {
                element.style.transform = `translate(${xPosition})`
            }, 100 * index)
        })

        setTimeout(function () {
            stateElement.disabled = false
        }, 100 * elementList.length)
    }

    const hideAllExcept = function (except) {
        firstLevelMenuItemStateList.forEach(function (item) {
            if (item == except) return
            if (!item.checked) return
            toggleSubmenu(item, false)
        })
    }

    const toggleSubmenu = function (stateElement, isOpened) {
        stateElement.checked = isOpened
        stateElement.disabled = true
        const submenu = stateElement.parentElement.querySelector(
            '.slide-down-on-toggle'
        )

        const height = submenu.scrollHeight
        if (isOpened) {
            submenu.style.height = `${height}px`
            setTimeout(function () {
                if (!isOpened) return
                submenu.style.height = 'auto'
                stateElement.disabled = false
            }, 600)
        } else {
            submenu.style.height = `${height}px`
            setTimeout(function () {
                if (isOpened) return
                submenu.style.height = '0px'
                stateElement.disabled = false
            }, 10)
        }

        toggleSubmenuItems(submenu, isOpened)
    }

    const toggleSubmenuItems = function (submenu, isOpened) {
        const opacity = isOpened ? '1' : '0'
        const elementList = Array.from(submenu.children)
        if (!isOpened) elementList.reverse()

        elementList.forEach(function (element, index) {
            setTimeout(function () {
                element.style.opacity = opacity
            }, 50 * (index + 1))
        })
    }
}

new IndexController().init()
