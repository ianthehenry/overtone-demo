(ns overdemo.demo
  (:use [overtone.live])
  (:use [overtone.studio.scope]))

(demo 1 (pan2 (sin-osc 440)))
