<script lang="ts">
    import { onMount } from "svelte";

    export let onActiveSectionChange: (index: number) => void = () => undefined;
    export let onScrolledChange: (scrolled: boolean) => void = () => undefined;

    let scrollTarget: HTMLElement;

    function updateActiveSection(scrollContainer: HTMLElement) {
        const sections = [...scrollContainer.querySelectorAll<HTMLElement>("[data-section]")];
        if (!sections.length) return;

        const viewportCenter = scrollContainer.scrollTop + scrollContainer.clientHeight / 2;
        let currentSection = sections[0];

        sections.slice(1).some((section) => {
            if (section.offsetTop > viewportCenter) return true;
            currentSection = section;
            return false;
        });

        onActiveSectionChange(Number(currentSection.dataset.section));
    }

    function handleScroll(event: Event) {
        const scrollContainer = event.currentTarget as HTMLElement;
        onScrolledChange(scrollContainer.scrollTop > 8);
        updateActiveSection(scrollContainer);
    }

    export function scrollToSection(index: number) {
        const target = scrollTarget?.querySelector<HTMLElement>(`[data-section="${index}"]`);
        onActiveSectionChange(index);
        onScrolledChange(index > 0);
        target?.scrollIntoView({ behavior: "smooth", block: "start" });
    }

    onMount(() => updateActiveSection(scrollTarget));
</script>

<div class="page-scroll" bind:this={scrollTarget} onscroll={handleScroll}>
    <slot />
</div>

<style>
    .page-scroll {
        height: 100svh;
        overflow-y: auto;
        overflow-x: hidden;
        scroll-snap-type: y mandatory;
        scroll-behavior: smooth;
        scroll-timeline-name: --page-scroll;
        scroll-timeline-axis: block;
        scrollbar-width: none;
    }

    .page-scroll::-webkit-scrollbar {
        display: none;
    }
</style>
