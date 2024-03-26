package com.ym.bookingservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {

	private String id;
	private String restaurantName;
	private String location;
	private String type;
	private List<BookTable> tables;
}
