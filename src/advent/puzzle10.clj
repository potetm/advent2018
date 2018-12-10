(ns advent.puzzle10
  (:require [clojure.string :as str]))

(defn read-sample
  []
  (with-open [f (-> "10/sample.txt"
                    clojure.java.io/reader)]
    (vec (line-seq f))))

(defn read-input
  []
  (with-open [f (-> "10/input.txt"
                    clojure.java.io/reader)]
    (vec (line-seq f))))

(def regex
  #"position=<\s*([-+]?\d+),\s*([-+]?\d+)> velocity=<\s*([-+]?\d+),\s*([-+]?\d+)>")

(defn parse
  [line]
  (->> (re-matches regex line)
       rest
       (map #(Long/parseLong %))
       (zipmap [:x :y :delta-x :delta-y])))

(defn measure
  [points]
  (->> [(map :x points) (map :y points)]
       (mapv (juxt (partial apply min) (partial apply max)))))

(defn transform
  [points]
  (->> points
       (mapv (fn [{:keys [x y delta-x delta-y], :as point}]
               (assoc point
                      :x (+ x delta-x)
                      :y (+ y delta-y))))))

(defn print-grid
  [bounding-box points]
  (let [[[x-start x-end] [y-start y-end]] bounding-box
        grid (-> points
                 (clojure.set/index [:x :y]))]
    (doseq [y (range y-start (inc y-end))]
      (->> (range x-start (inc x-end))
           (map (fn [x] (if (grid {:x x, :y y}) "*" ".")))
           (str/join)
           println))
    (println)))

(defn solution-1
  []
  (let [initial-points (->> (read-sample)
                            (mapv parse))
        bounding-box (measure initial-points)]
    (->> initial-points
         (iterate transform)
         (take 4)
         (run! (partial print-grid bounding-box)))))