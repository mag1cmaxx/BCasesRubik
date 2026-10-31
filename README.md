**Русский** | [English](README.en.md) | [Українська](README.uk.md)

# Rubik

[![Build](https://github.com/mag1cmaxx/BCasesRubik/actions/workflows/build.yml/badge.svg)](https://github.com/mag1cmaxx/BCasesRubik/actions/workflows/build.yml)
[![Release](https://img.shields.io/github/v/release/mag1cmaxx/BCasesRubik?label=version&color=blue)](https://github.com/mag1cmaxx/BCasesRubik/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/mag1cmaxx/BCasesRubik/total?color=blue)](https://github.com/mag1cmaxx/BCasesRubik/releases)
![Paper](https://img.shields.io/badge/Paper-1.21.4%2B-2e7d32)
![Java](https://img.shields.io/badge/Java-21-b07219)

Аддон для [BCases](https://github.com/By1337/BCases): над кейсом появляется объёмный кубик Рубика 3×3×3 и сам себя собирает.


https://github.com/user-attachments/assets/a96428cb-94e8-4213-95d5-bef07db20eeb


1. Детали слетаются из искр и складываются в собранный кубик.
2. Кубик крутится в воздухе и быстро перемешивается, каждый поворот слоя виден целиком.
3. Потом он собирается обратно. Последние повороты идут медленнее.
4. Собранный кубик вспыхивает, разлетается на детали и оставляет внутри выигрыш.

Кубик состоит из 81 block display: 27 чёрных деталей и 54 наклейки. Всё сделано на пакетах через VirtualEntityApi, в мир ничего не ставится. Приз выбирается заранее, как в остальных анимациях BCases.

## Требования

- Paper 1.21.4 или новее
- BCases 1.2
- VirtualEntityApi с поддержкой версии сервера

## Установка

1. Скачать `BCasesRubik-<версия>.jar` из [релизов](https://github.com/mag1cmaxx/BCasesRubik/releases/latest).
2. Положить его в `plugins/BCases/addons/`.
3. Перезапустить сервер или выполнить `/bcases reload`.
4. Появится файл `plugins/BCases/animations/rubik.yml`, id анимации `rubik`.

Проверить: посмотреть на блок кейса и ввести `/bcases play rubik <набор призов>`.

## Настройка

Все параметры описаны в `rubik.yml`. Любой из них можно удалить, тогда возьмётся значение по умолчанию.

| Параметр | По умолчанию | Что делает |
|---|---|---|
| `height` | 2.3 | высота центра кубика над низом блока кейса |
| `size` | 0.45 | размер одной детали в блоках, весь кубик в три раза больше |
| `spin` | 3.0 | скорость вращения всего кубика, градусов за тик |
| `tilt` | 30 | наклон, чтобы была видна верхняя грань |
| `scramble.moves` | 20 | сколько поворотов при перемешивании |
| `scramble.ticks` | 2 | тиков на поворот при перемешивании |
| `solve.ticks` | 4 | тиков на поворот при сборке |
| `solve.final_ticks` / `solve.final_moves` | 10 / 3 | замедление последних поворотов |
| `show_ticks` | 80 | сколько тиков показывать приз |
| `colors.*` | бетон | блоки граней и деталей, можно поставить любые |

Длительность всей анимации примерно `moves × (scramble.ticks + solve.ticks)` плюс около 5 секунд на сборку, вспышку и показ приза.

## Сборка

```
mvn clean package
```

Нужна Java 21. Готовый jar будет в `target/`.
