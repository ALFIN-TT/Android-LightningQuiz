package com.cube.storm.ui.quiz.model.quiz;

import com.cube.storm.ui.model.property.ImageProperty;
import com.cube.storm.ui.quiz.model.property.ZoneProperty;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Quiz item for selecting an area on an image
 *
 * @author Matt Allen
 * @project Hazards
 */
@NoArgsConstructor @AllArgsConstructor(suppressConstructorProperties = true)
@Accessors(chain = true) @Data
public class AreaQuizItem extends QuizItem
{
	protected ArrayList<ImageProperty> image;
	protected ArrayList<ZoneProperty> answer;
}
